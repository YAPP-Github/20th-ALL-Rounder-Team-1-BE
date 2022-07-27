package com.yapp.weekand.domain.schedule.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.PaginationInfo
import com.yapp.weekand.api.generated.types.Schedule
import com.yapp.weekand.api.generated.types.ScheduleCategory
import com.yapp.weekand.api.generated.types.ScheduleList
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.category.entity.ScheduleCategoryOpenType
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.entity.Status
import com.yapp.weekand.domain.schedule.service.ScheduleService
import com.yapp.weekand.domain.sticker.entity.ScheduleStickerName
import com.yapp.weekand.domain.user.service.UserService
import java.time.LocalDateTime
import java.util.*
import kotlin.math.abs

@DgsComponent
class ScheduleQueryResolver(
	private val scheduleService: ScheduleService,
	private val userService: UserService
) {
	@DgsQuery
	@JwtAuth
	fun schedules(@InputArgument date: LocalDateTime, @InputArgument userId: String?): List<ScheduleRule> {
		return if (userId == null) {
			scheduleService.getUserSchedulesByDate(date, userService.getCurrentUser().id)
		} else {
			scheduleService.getUserSchedulesByDate(date, userId.toLong())
		}
	}

	@DgsQuery
	@JwtAuth
	fun schedule(@InputArgument scheduleId: Long) = scheduleService.getSchedule(scheduleId)
}
