package com.yapp.weekand.domain.follow.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.yapp.weekand.domain.follow.dto.FollowDto
import com.yapp.weekand.domain.follow.service.FollowService
import com.yapp.weekand.domain.user.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class FollowResolver (
	private val followService: FollowService,
	private val userService: UserService
) {
	@DgsQuery
	@PreAuthorize("isAuthenticated()")
	fun followers(page:Int, size:Int): FollowDto.FollowerList {
		val followers = followService.getFollowers(userService.getCurrentUser(), PageRequest.of(page, size))
		return FollowDto.FollowerList(FollowDto.PaginationInfo(followers.hasNext()), followers = followers.content)
	}

	@DgsQuery
	@PreAuthorize("isAuthenticated()")
	fun followees(page:Int, size: Int): FollowDto.FolloweeList {
		val followees = followService.getFollowees(userService.getCurrentUser(), PageRequest.of(page, size))
		return FollowDto.FolloweeList(FollowDto.PaginationInfo(followees.hasNext()), followees = followees.content)
	}
}
