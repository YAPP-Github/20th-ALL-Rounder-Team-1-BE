package com.yapp.weekand.domain.user.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.UpdateUserProfileInput
import com.yapp.weekand.domain.user.service.UserService

@DgsComponent
class UserMutationResolver(
	private val userService: UserService
) {
	@DgsMutation
	fun inquiry(@InputArgument contents: String): Boolean {
		userService.sendInquiryMail(userService.getCurrentUser(), contents)
		return true
	}

	@DgsMutation
	fun updateUserProfile(@InputArgument input: UpdateUserProfileInput): Boolean {
		userService.updateUserProfile(userService.getCurrentUser().id, input)
		return true
	}
}
