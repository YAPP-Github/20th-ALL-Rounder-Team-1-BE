package com.yapp.weekand.domain.category.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.PaginationInfo
import com.yapp.weekand.api.generated.types.ScheduleCategoryList
import com.yapp.weekand.api.generated.types.ScheduleCategorySort
import com.yapp.weekand.api.generated.types.SearchScheduleList
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.category.service.ScheduleCategoryService
import com.yapp.weekand.domain.user.service.UserService

@DgsComponent
class ScheduleCategoryQueryResolver(
	private val scheduleCategoryService: ScheduleCategoryService,
	private val userService: UserService
) {
	@DgsQuery
	@JwtAuth
	fun scheduleCategories(
		@InputArgument sort: ScheduleCategorySort,
		@InputArgument page: Int,
		@InputArgument size: Int
	): ScheduleCategoryList {
		val scheduleCategories =
			scheduleCategoryService.getScheduleCategories(userService.getCurrentUser(), sort, page, size)
		return ScheduleCategoryList(
			paginationInfo = PaginationInfo(scheduleCategories.hasNext()),
			scheduleCategories = scheduleCategories.content
		)
	}

	@DgsQuery
	@JwtAuth
	fun searchSchedules (
		@InputArgument sort: ScheduleCategorySort,
		@InputArgument page: Int,
		@InputArgument size: Int,
		@InputArgument searchQuery: String?,
		@InputArgument categoryId: Long
	): SearchScheduleList {
		val searchSchedules = scheduleCategoryService.searchSchedules(sort, page, size, searchQuery, categoryId)
		return SearchScheduleList(
			paginationInfo = PaginationInfo(searchSchedules.hasNext()),
			schedules = searchSchedules.content
		)
	}
}
