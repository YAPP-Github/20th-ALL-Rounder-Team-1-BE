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
	fun tmpScheduleListGen(count: Int): List<Schedule> = IntArray(count) { it + 1 }.map {
		Schedule(
			id = it.toString(),
			name = "user_${it}",
			status = listOf(Status.UNDETERMINED, Status.COMPLETED, Status.INCOMPLETED)[it % 3],
			category = ScheduleCategory(
				id = it.toString(),
				name = "카테고리_${it}",
				color = "red",
				openType = ScheduleCategoryOpenType.ALL_OPEN,
			),
			dateTimeStart = LocalDateTime.now(),
			dateTimeEnd = LocalDateTime.now(),
			stickerCount = abs(Random().nextInt() % 100),
			stickerNames = listOf(ScheduleStickerName.CHEER_UP, ScheduleStickerName.COOL),
		)
	}

	@DgsQuery
	@JwtAuth
	fun schedules(@InputArgument date: LocalDateTime, @InputArgument userId: String?): List<ScheduleRule> {
		if (userId == null) {
			return scheduleService.getUserSchedulesByDate(date, userService.getCurrentUser().id)
		} else {
			return scheduleService.getUserSchedulesByDate(date, userId.toLong())
		}
	}

	@DgsQuery
	@JwtAuth
	fun schedule(@InputArgument scheduleId: Long) = scheduleService.getSchedule(scheduleId)
}
