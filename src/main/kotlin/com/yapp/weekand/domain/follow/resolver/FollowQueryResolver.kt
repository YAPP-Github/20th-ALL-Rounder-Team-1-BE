package com.yapp.weekand.domain.follow.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.yapp.weekand.api.generated.types.FollowUser
import com.yapp.weekand.api.generated.types.FolloweeList
import com.yapp.weekand.api.generated.types.FollowerList
import com.yapp.weekand.api.generated.types.PaginationInfo
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.follow.service.FollowService
import com.yapp.weekand.domain.user.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice

@DgsComponent
class FollowQueryResolver(
	private val followService: FollowService,
	private val userService: UserService
) {
	@DgsQuery
	@JwtAuth
	fun followers(page: Int, size: Int, userId: Long?): FollowerList {
		val followers: Slice<FollowUser> =
			if (userId == null) {
				followService.getFollowers(userService.getCurrentUser().id, PageRequest.of(page, size))
			} else {
				followService.getFollowers(userId, PageRequest.of(page, size))
			}
		return FollowerList(PaginationInfo(followers.hasNext()), followers = followers.content)
	}

	@DgsQuery
	@JwtAuth
	fun followees(page: Int, size: Int, userId: Long?): FolloweeList {
		val followees: Slice<FollowUser> =
			if (userId == null) {
				followService.getFollowees(userService.getCurrentUser().id, PageRequest.of(page, size))
			} else {
				followService.getFollowees(userId, PageRequest.of(page, size))
			}
		return FolloweeList(PaginationInfo(followees.hasNext()), followees = followees.content)
	}
}
