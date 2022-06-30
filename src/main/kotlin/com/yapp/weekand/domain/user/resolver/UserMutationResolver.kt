package com.yapp.weekand.domain.user.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.domain.user.service.UserService
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class UserMutationResolver (
	private val userService: UserService
) {
	@DgsMutation
	@PreAuthorize("isAuthenticated()")
	fun updateInterests(@InputArgument interests: List<String>): String = userService.updateInterests(userService.getCurrentUser(), interests)
}
