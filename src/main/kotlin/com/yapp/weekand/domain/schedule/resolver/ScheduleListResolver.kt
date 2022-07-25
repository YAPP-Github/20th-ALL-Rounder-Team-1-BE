package com.yapp.weekand.domain.schedule.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.yapp.weekand.api.generated.types.PaginationInfo
import com.yapp.weekand.api.generated.types.Schedule
import com.yapp.weekand.api.generated.types.ScheduleCategory
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.entity.ScheduleStatus
import com.yapp.weekand.domain.schedule.entity.Status
import com.yapp.weekand.domain.sticker.entity.ScheduleStickerName

@DgsComponent
class ScheduleListResolver {
	@DgsData(parentType = "ScheduleList")
	fun paginationInfo(): PaginationInfo = PaginationInfo(hasNext = false)

	@DgsData(parentType = "ScheduleList")
	fun schedules(dfe: DgsDataFetchingEnvironment): List<Schedule> {
		val scheduleRules = dfe.getSource<List<ScheduleRule>>()
		return scheduleRules.map { scheduleRule ->
			Schedule(
				id = scheduleRule.id.toString(),
				name = scheduleRule.name,
				status = scheduleRule.scheduleStatus
					.find { status -> status.dateYmd == scheduleRule.dateStart.toLocalDate() }?.status
					?: Status.UNDETERMINED,
				category = ScheduleCategory(
					id = scheduleRule.scheduleCategory.id.toString(),
					name = scheduleRule.scheduleCategory.name,
					color = scheduleRule.scheduleCategory.color,
					openType = scheduleRule.scheduleCategory.openType
				),
				dateTimeStart = scheduleRule.dateStart,
				dateTimeEnd = scheduleRule.dateEnd,
				stickerCount = 0,
				stickerNames = listOf(ScheduleStickerName.CHEER_UP),
			)
		}
	}
}
