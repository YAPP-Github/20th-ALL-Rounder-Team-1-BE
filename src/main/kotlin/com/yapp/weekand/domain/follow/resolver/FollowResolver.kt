package com.yapp.weekand.domain.follow.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
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
	fun getFollowers(page:Int, size:Int): FollowDto.getFollowsResponse {
		val followers = followService.getFollowers(userService.getCurrentUser(), PageRequest.of(page, size))
		return FollowDto.getFollowsResponse(FollowDto.PageInfo(followers.hasNext()), follows = followers.content)
	}
}
