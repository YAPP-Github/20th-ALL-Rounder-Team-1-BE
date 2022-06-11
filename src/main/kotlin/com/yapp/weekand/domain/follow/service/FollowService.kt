package com.yapp.weekand.domain.follow.service

import com.yapp.weekand.domain.follow.dto.FollowDto
import com.yapp.weekand.domain.follow.entity.Follow
import com.yapp.weekand.domain.follow.repository.FollowRepository
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FollowService(
	private val followRepository: FollowRepository
) {
	fun getFollowers(user: User, pageable: Pageable): Slice<FollowDto.Follows> {
		return followRepository.findByFolloweeUserOrderByDateCreated(user, pageable)
			.map(Follow::followerUser)
			.map{ FollowDto.Follows(id = it.id, nickname = it.nickname, goal = it.goal, profileFilename = it.profileFilename) }
	}
}
