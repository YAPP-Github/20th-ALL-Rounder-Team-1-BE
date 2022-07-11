package com.yapp.weekand.domain.follow.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.follow.dto.FollowDto
import com.yapp.weekand.domain.follow.service.FollowService
import com.yapp.weekand.domain.user.service.UserService
import org.springframework.data.domain.PageRequest

@DgsComponent
class FollowResolver (
	private val followService: FollowService,
	private val userService: UserService
) {
	@DgsQuery
	@JwtAuth
	fun followers(page:Int, size:Int): FollowDto.FollowerList {
		val followers = followService.getFollowers(userService.getCurrentUser(), PageRequest.of(page, size))
		return FollowDto.FollowerList(FollowDto.PaginationInfo(followers.hasNext()), followers = followers.content)
	}

	@DgsQuery
	@JwtAuth
	fun followees(page:Int, size: Int): FollowDto.FolloweeList {
		val followees = followService.getFollowees(userService.getCurrentUser(), PageRequest.of(page, size))
		return FollowDto.FolloweeList(FollowDto.PaginationInfo(followees.hasNext()), followees = followees.content)
	}
}
