package com.yapp.weekand.domain.follow.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.yapp.weekand.api.generated.types.FollowUser
import com.yapp.weekand.domain.user.service.UserService

@DgsComponent
class FollowUserResolver(
	private val userService: UserService
) {
	@DgsData(parentType = "FollowUser")
	fun profileImageUrl(dfe: DgsDataFetchingEnvironment): String {
		val targetUser = dfe.getSource<FollowUser>()
		return userService.getUserProfileImageUrl(targetUser.id.toLong())
	}
}
