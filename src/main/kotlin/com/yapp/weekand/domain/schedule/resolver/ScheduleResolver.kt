package com.yapp.weekand.domain.schedule.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.yapp.weekand.api.generated.types.Schedule
import com.yapp.weekand.domain.sticker.entity.ScheduleStickerName
import com.yapp.weekand.domain.sticker.service.ScheduleStickerService

@DgsComponent
class ScheduleResolver(
	private val scheduleStickerService: ScheduleStickerService,
) {
	@DgsData(parentType = "Schedule")
	fun stickerCount(dfe: DgsDataFetchingEnvironment): Int {
		val schedule = dfe.getSource<Schedule>()
		val scheduleStickerSummary =
			scheduleStickerService.getScheduleStickerSummary(schedule.id.toLong(), schedule.dateTimeStart)
		return scheduleStickerSummary.totalCount
	}

	@DgsData(parentType = "Schedule")
	fun stickerNames(dfe: DgsDataFetchingEnvironment): List<ScheduleStickerName> {
		val schedule = dfe.getSource<Schedule>()
		val scheduleStickerSummary =
			scheduleStickerService.getScheduleStickerSummary(schedule.id.toLong(), schedule.dateTimeStart)
		return scheduleStickerSummary.scheduleStickers.map { it.name }
	}
}
