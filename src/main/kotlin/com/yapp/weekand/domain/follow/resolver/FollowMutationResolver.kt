package com.yapp.weekand.domain.follow.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.CreateFollowInput
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.follow.service.FollowService
import com.yapp.weekand.domain.user.service.UserService

@DgsComponent
class FollowMutationResolver(
	private val followService: FollowService,
	private val userService: UserService,
) {
	@DgsMutation
	@JwtAuth
	fun createFollow(@InputArgument input: CreateFollowInput): Boolean {
		followService.createFollow(userService.getCurrentUser(), input.targetUserId.toLong())
		return true;
	}
}
