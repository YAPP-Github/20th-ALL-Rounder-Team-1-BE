package com.yapp.weekand.domain.schedule.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.PaginationInfo
import com.yapp.weekand.api.generated.types.Schedule
import com.yapp.weekand.api.generated.types.ScheduleCategory
import com.yapp.weekand.api.generated.types.ScheduleList
import com.yapp.weekand.domain.category.entity.ScheduleCategoryOpenType
import com.yapp.weekand.domain.schedule.entity.Status
import com.yapp.weekand.domain.sticker.entity.ScheduleStickerName
import java.time.LocalTime
import java.util.*

@DgsComponent
class ScheduleQueryResolver {
	fun tmpScheduleListGen(count: Int): List<Schedule> = IntArray(count) { it + 1 }.map {
		Schedule(
			id = it.toString(),
			name = "user_${it}",
			status = listOf(Status.UNDETERMINED, Status.COMPLETED, Status.UNCOMPLETED)[it % 3],
			category = ScheduleCategory(
				id = it.toString(),
				name = "카테고리_${it}",
				color = "red",
				openType = ScheduleCategoryOpenType.ALL_OPEN,
			),
			dateTimeStart = LocalTime.now().toString(),
			dateTimeEnd = LocalTime.now().toString(),
			stickerCount = Random().nextInt() % 100,
			stickerNames = listOf(ScheduleStickerName.CHEER_UP, ScheduleStickerName.COOL),
		)
	}

	@DgsQuery
	fun schedules(@InputArgument date: String): ScheduleList {
		return ScheduleList(
			paginationInfo = PaginationInfo(hasNext = false),
			schedules = tmpScheduleListGen(30),
		)
	}
}
