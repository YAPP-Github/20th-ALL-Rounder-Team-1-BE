package com.yapp.weekand.domain.follow.service

import com.yapp.weekand.api.generated.types.FollowUser
import com.yapp.weekand.domain.auth.exception.UserNotFoundException
import com.yapp.weekand.domain.follow.entity.Follow
import com.yapp.weekand.domain.follow.exception.FollowDuplicatedException
import com.yapp.weekand.domain.follow.exception.FollowNotFoundException
import com.yapp.weekand.domain.follow.mapper.toFollowUserGraphql
import com.yapp.weekand.domain.follow.repository.FollowRepository
import com.yapp.weekand.domain.notification.entity.NotificationType
import com.yapp.weekand.domain.notification.service.NotificationService
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.user.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FollowService(
	private val followRepository: FollowRepository,
	private val userRepository: UserRepository,
	private val notificationService: NotificationService
) {
	fun getFollowers(userId: Long, pageable: Pageable): Slice<FollowUser> {
		val user: User = userRepository.findByIdOrNull(userId)
			?: throw UserNotFoundException()

		return followRepository.findByFolloweeUserOrderByDateCreatedDesc(user, pageable)
			.map(Follow::followerUser)
			.map {
				it.toFollowUserGraphql()
			}
	}

	fun getFollowees(userId: Long, pageable: Pageable): Slice<FollowUser> {
		val user: User = userRepository.findByIdOrNull(userId)
			?: throw UserNotFoundException()

		return followRepository.findByFollowerUserOrderByDateCreatedDesc(user, pageable)
			.map(Follow::followeeUser)
			.map {
				it.toFollowUserGraphql()
			}
	}

	@Transactional
	fun createFollow(user: User, targetUserId: Long) {
		val targetUser = userRepository.findByIdOrNull(targetUserId)
			?: throw UserNotFoundException()

		val existedFollow = followRepository.existsByFollowerUserAndFolloweeUser(user, targetUser)
		if (existedFollow) {
			throw FollowDuplicatedException()
		}

		followRepository.save(
			Follow(
				followerUser = user,
				followeeUser = targetUser,
			)
		)
		targetUser.plusFollowerCount()

		val notiMessage = "${user.nickname}님이 팔로우하였습니다"
		notificationService.addNotifications(targetUser, NotificationType.FOLLOW, notiMessage)
	}

	@Transactional
	fun deleteFollowByUser(user: User) {
		val follows = followRepository.findByFollowerUserOrFolloweeUser(user, user)
		followRepository.deleteAllInBatch(follows)
	}

	@Transactional
	fun deleteFollower(user: User, targetUserId: Long) {
		val targetUser = userRepository.findByIdOrNull(targetUserId)
			?: throw UserNotFoundException()

		val follow = followRepository.findByFollowerUserAndFolloweeUser(targetUser, user)
			?: throw FollowNotFoundException()

		followRepository.delete(follow)
		user.minusFollowerCount()
	}

	@Transactional
	fun deleteFollowee(user: User, targetUserId: Long) {
		val targetUser = userRepository.findByIdOrNull(targetUserId)
			?: throw UserNotFoundException()

		val follow = followRepository.findByFollowerUserAndFolloweeUser(user, targetUser)
			?: throw FollowNotFoundException()

		followRepository.delete(follow)
		targetUser.minusFollowerCount()
	}

	fun isFollowed(user: User, targetUserId: Long): Boolean {
		val targetUser = userRepository.findByIdOrNull(targetUserId) ?: return false

		return followRepository.existsByFollowerUserAndFolloweeUser(followerUser = user, followeeUser = targetUser)
	}
}
