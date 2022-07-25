package com.yapp.weekand.domain.schedule.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.*
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.category.entity.ScheduleCategoryOpenType
import com.yapp.weekand.domain.schedule.entity.Status
import com.yapp.weekand.domain.schedule.service.ScheduleService
import com.yapp.weekand.domain.sticker.entity.ScheduleStickerName
import graphql.execution.DataFetcherResult
import java.time.LocalDateTime
import java.util.*
import kotlin.math.abs

@DgsComponent
class ScheduleQueryResolver(
	private val scheduleService: ScheduleService
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
	fun schedules(@InputArgument date: LocalDateTime): ScheduleList {
		return ScheduleList(
			paginationInfo = PaginationInfo(hasNext = false),
			schedules = tmpScheduleListGen(30),
		)
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
