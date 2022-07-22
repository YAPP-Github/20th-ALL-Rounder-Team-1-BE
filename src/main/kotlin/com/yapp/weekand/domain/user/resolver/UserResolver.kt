package com.yapp.weekand.domain.user.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.yapp.weekand.domain.follow.service.FollowService
import com.yapp.weekand.api.generated.types.User as UserGraphql
import com.yapp.weekand.domain.user.service.UserService

@DgsComponent
class UserResolver(
	private val userService: UserService,
	private val followService: FollowService
) {
	@DgsData(parentType = "User")
	fun followed(dfe: DgsDataFetchingEnvironment): Boolean {
		val currentUser = userService.getCurrentUser()
		val targetUser = dfe.getSource<UserGraphql>()
		return followService.isFollowed(currentUser, targetUser.id.toLong())
	}
}
