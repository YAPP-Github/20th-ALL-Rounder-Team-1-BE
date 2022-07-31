package com.yapp.weekand.domain.schedule.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.ScheduleInfo
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.service.ScheduleService
import com.yapp.weekand.domain.user.service.UserService
import graphql.execution.DataFetcherResult
import java.time.LocalDateTime

@DgsComponent
class ScheduleQueryResolver(
	private val scheduleService: ScheduleService,
	private val userService: UserService
) {
	@DgsQuery
	@JwtAuth
	fun schedules(@InputArgument date: LocalDateTime, @InputArgument userId: String?): DataFetcherResult<List<ScheduleRule>> {
		val currentUserId = userService.getCurrentUser().id
		val args = mapOf("date" to date)

		val schedules =
			if (userId == null) {
				scheduleService.getUserSchedulesByDate(date, currentUserId, currentUserId)
			} else {
				scheduleService.getUserSchedulesByDate(date, userId.toLong(), currentUserId)
			}

		return DataFetcherResult.newResult<List<ScheduleRule>>()
			.data(schedules)
			.localContext(args)
			.build()
	}

	@DgsQuery
	@JwtAuth
	fun scheduleRule(@InputArgument scheduleId: Long) = scheduleService.getScheduleRule(scheduleId)

	@DgsQuery
	@JwtAuth
	fun schedule(
		@InputArgument scheduleId: Long,
		@InputArgument date: LocalDateTime
	): DataFetcherResult<ScheduleInfo> {
		val schedule = scheduleService.getSchedule(scheduleId, date.toLocalDate())
		val args = mapOf("date" to date)
		return DataFetcherResult.newResult<ScheduleInfo>()
			.data(schedule)
			.localContext(args)
			.build()
	}
}
