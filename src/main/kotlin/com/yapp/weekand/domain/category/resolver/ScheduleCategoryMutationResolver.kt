package com.yapp.weekand.domain.category.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.DeleteScheduleCategoryInput
import com.yapp.weekand.api.generated.types.ScheduleCategoryInput
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.category.service.ScheduleCategoryService
import com.yapp.weekand.domain.user.service.UserService

@DgsComponent
class ScheduleCategoryMutationResolver(
	private val scheduleCategoryService: ScheduleCategoryService,
	private val userService: UserService
) {
	@DgsMutation
	@JwtAuth
	fun createCategory(@InputArgument scheduleCategoryInput: ScheduleCategoryInput) =
		scheduleCategoryService.createCategory(scheduleCategoryInput, userService.getCurrentUser())

	@DgsMutation
	@JwtAuth
	fun updateCategory(@InputArgument categoryId: Long,
					   @InputArgument scheduleCategoryInput: ScheduleCategoryInput) =
		scheduleCategoryService.updateCategory(categoryId, scheduleCategoryInput, userService.getCurrentUser())

	@DgsMutation
	@JwtAuth
	fun deleteCategory(@InputArgument input: DeleteScheduleCategoryInput): Boolean {
		scheduleCategoryService.deleteCategory(input.scheduleCategoryId.toLong(), userService.getCurrentUser())
		return true
	}
}
