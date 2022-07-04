package com.yapp.weekand.domain.category.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.ScheduleCategoryInput
import com.yapp.weekand.domain.category.service.ScheduleCategoryService
import com.yapp.weekand.domain.user.service.UserService
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class ScheduleCategoryMutationResolver(
	private val scheduleCategoryService: ScheduleCategoryService,
	private val userService: UserService
) {
	@DgsMutation
	@PreAuthorize("isAuthenticated()")
	fun createCategory(@InputArgument scheduleCategoryInput: ScheduleCategoryInput) = scheduleCategoryService.createCategory(scheduleCategoryInput, userService.getCurrentUser())
}
