package com.yapp.weekand.domain.user.service

import com.yapp.weekand.api.generated.types.SearchUserSort
import com.yapp.weekand.api.generated.types.UpdateUserProfileInput
import com.yapp.weekand.api.generated.types.UserProfileImageExtensionType
import com.yapp.weekand.api.generated.types.UserProfileImageS3PresignedUrl
import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.auth.exception.NicknameDuplicatedException
import com.yapp.weekand.domain.auth.exception.UserNotFoundException
import com.yapp.weekand.domain.interest.service.InterestService
import com.yapp.weekand.domain.job.service.JobService
import com.yapp.weekand.domain.user.dto.SearchUserListCondition
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.user.exception.GoalMaxLengthExceedException
import com.yapp.weekand.domain.user.exception.NicknameMaxLengthExceedException
import com.yapp.weekand.domain.user.exception.NicknameUnderMinLengthException
import com.yapp.weekand.domain.user.repository.UserRepository
import com.yapp.weekand.infra.email.EmailService
import com.yapp.weekand.infra.email.replacement.InquiryEmailReplacement
import com.yapp.weekand.infra.s3.S3Service
import org.joda.time.LocalDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
	private val userRepository: UserRepository,
	private val jwtProvider: JwtProvider,
	private val emailService: EmailService,
	private val jobService: JobService,
	private val interestService: InterestService,
	private val s3Service: S3Service,
) {
	@Value("\${spring.mail.username}")
	private lateinit var WEEKAND_EMAIL: String

	fun checkDuplicateNickname(nickname: String) = userRepository.existsUserByNickname(nickname)

	fun findUserById(id: Long) = userRepository.findByIdOrNull(id)

	fun searchUsers(
		searchQuery: String?,
		jobs: List<String>?,
		interests: List<String>?,
		sort: SearchUserSort?,
		pageable: Pageable
	) =
		userRepository.searchUserListWithPaging(
			SearchUserListCondition(
				nickNameOrGoalQuery = searchQuery,
				jobs = jobs,
				interests = interests,
				sort = sort
			), pageable
		)

	fun getCurrentUser(): User {
		val currentUserId = jwtProvider.getFromSecurityContextHolder().user.id
		return userRepository.findByIdOrNull(currentUserId)
			?: throw UserNotFoundException()
	}

	fun sendInquiryMail(user: User, contents: String) {
		val replacements = InquiryEmailReplacement(
			mapOf("userEmail" to user.email, "contents" to contents),
			"${user.email} 님의 문의 접수입니다."
		)
		emailService.sendEmail(WEEKAND_EMAIL, replacements)
	}

	@Transactional
	fun updateUserProfile(userId: Long, input: UpdateUserProfileInput) {
		if (input.goal.length > GOAL_MAX_LENGTH) {
			throw GoalMaxLengthExceedException()
		}

		if (input.nickname.length > NICKNAME_MAX_LENGTH) {
			throw NicknameMaxLengthExceedException()
		}

		if (input.nickname.length < NICKNAME_MIN_LENGTH) {
			throw NicknameUnderMinLengthException()
		}

		val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

		if (user.nickname != input.nickname && this.checkDuplicateNickname(input.nickname)) {
			throw NicknameDuplicatedException()
		}

		user.profileImageFilename = input.profileImageFilename
		user.nickname = input.nickname
		user.goal = input.goal

		userRepository.save(user)

		jobService.deleteAllUserJob(user)
		interestService.deleteAllUserInterest(user)

		jobService.createUserJobList(user, input.jobs)
		interestService.createUserInterestList(user, input.interests)
	}

	fun getUserProfileImageUrl(userId: Long): String {
		val defaultProfileImageUrl = "${USER_PROFILE_IMAGE_URL_PREFIX}/default.png"

		val user = findUserById(userId) ?: return defaultProfileImageUrl

		if (user.profileImageFilename == null) {
			return defaultProfileImageUrl
		}
		return "${USER_PROFILE_IMAGE_URL_PREFIX}/${user.id}/${user.profileImageFilename}"
	}

	fun createUserProfileImageS3PresignedUrl(
		userId: Long,
		extension: UserProfileImageExtensionType
	): UserProfileImageS3PresignedUrl {
		val userProfileImageS3Path = "profile-images/${userId}"

		val postfix = LocalDateTime.now().toString("yyyy-MM-dd'T'HHmmss")
		val filename = "profile-${postfix}.${extension.toString().lowercase()}"

		val sigendUrl = s3Service.generatePresignedUrl("${userProfileImageS3Path}/${filename}")
		return UserProfileImageS3PresignedUrl(sigendUrl, filename)
	}

	companion object {
		const val GOAL_MAX_LENGTH = 20
		const val NICKNAME_MAX_LENGTH = 12
		const val NICKNAME_MIN_LENGTH = 2
		const val USER_PROFILE_IMAGE_URL_PREFIX = "https://weekand.s3.ap-northeast-2.amazonaws.com/profile-images"
	}
}
