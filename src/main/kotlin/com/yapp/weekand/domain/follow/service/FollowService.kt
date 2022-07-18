package com.yapp.weekand.domain.follow.service

import com.yapp.weekand.domain.auth.exception.UserNotFoundException
import com.yapp.weekand.domain.follow.dto.FollowDto
import com.yapp.weekand.domain.follow.entity.Follow
import com.yapp.weekand.domain.follow.exception.FollowDuplicatedException
import com.yapp.weekand.domain.follow.repository.FollowRepository
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.user.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FollowService(
	private val followRepository: FollowRepository,
	private val userRepository: UserRepository
) {
	fun getFollowers(user: User, pageable: Pageable): Slice<FollowDto.Follows> {
		return followRepository.findByFolloweeUserOrderByDateCreatedDesc(user, pageable)
			.map(Follow::followerUser)
			.map{ FollowDto.Follows(id = it.id, nickname = it.nickname, goal = it.goal, profileFilename = it.profileFilename) }
	}

	fun getFollowees(user:User, pageable: Pageable): Slice<FollowDto.Follows> {
		return followRepository.findByFollowerUserOrderByDateCreatedDesc(user, pageable)
			.map(Follow::followeeUser)
			.map { FollowDto.Follows(id = it.id, nickname = it.nickname, goal = it.goal, profileFilename = it.profileFilename) }
	}

	@Transactional
	fun createFollow(user: User, targetUserId: Long) {
		val targetUser = userRepository.findById(targetUserId)
		if(targetUser.isEmpty) {
			throw UserNotFoundException()
		}

		val existedFollow = followRepository.findByFollowerUserAndFolloweeUser(user, targetUser.get())
		if(!existedFollow.isEmpty){
			throw FollowDuplicatedException()
		}

		followRepository.save(
			Follow(
				followerUser = user,
				followeeUser = targetUser.get(),
			)
		)
	}
	@Transactional
	fun deleteFollowByUser(user: User) {
		val follows =  followRepository.findByFollowerUserOrFolloweeUser(user, user)
		followRepository.deleteAllInBatch(follows)
	}
}
