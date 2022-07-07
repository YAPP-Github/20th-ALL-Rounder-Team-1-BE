package com.yapp.weekand.domain.user.service

import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.auth.exception.UserNotFoundException
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.user.repository.UserRepository
import com.yapp.weekand.infra.email.EmailService
import com.yapp.weekand.infra.email.replacement.InquiryEmailReplacement
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService (
	private val userRepository: UserRepository,
	private val jwtProvider: JwtProvider,
	private val emailService: EmailService
){
	private val helpEmail: String = "help@week-and.kr"

	fun checkDuplicateNickname(nickname: String) = userRepository.existsUserByNickname(nickname)

	fun findUserById(id: Long) = userRepository.findById(id).get()

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
		emailService.sendEmail(helpEmail, replacements)
	}
}
