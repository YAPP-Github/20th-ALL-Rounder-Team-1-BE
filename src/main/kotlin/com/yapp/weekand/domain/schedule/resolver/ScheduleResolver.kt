package com.yapp.weekand.domain.schedule.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.yapp.weekand.api.generated.types.Schedule
import com.yapp.weekand.domain.schedule.entity.Status
import com.yapp.weekand.domain.schedule.service.ScheduleService
import com.yapp.weekand.domain.sticker.entity.ScheduleStickerName
import com.yapp.weekand.domain.sticker.service.ScheduleStickerService
import java.time.LocalDateTime

@DgsComponent
class ScheduleResolver(
	private val scheduleStickerService: ScheduleStickerService,
	private val scheduleService: ScheduleService
) {
	@DgsData(parentType = "Schedule")
	fun stickerCount(dfe: DgsDataFetchingEnvironment): Int {
		val schedule = dfe.getSource<Schedule>()
		val args: Map<String, LocalDateTime> = dfe.getLocalContext()
		val date = args["date"]!!
		val scheduleStickerSummary =
			scheduleStickerService.getScheduleStickerSummary(schedule.id.toLong(), date)
		return scheduleStickerSummary.totalCount
	}

	@DgsData(parentType = "Schedule")
	fun stickerNames(dfe: DgsDataFetchingEnvironment): List<ScheduleStickerName> {
		val schedule = dfe.getSource<Schedule>()
		val args: Map<String, LocalDateTime> = dfe.getLocalContext()
		val date = args["date"]!!
		val scheduleStickerSummary =
			scheduleStickerService.getScheduleStickerSummary(schedule.id.toLong(), date)
		return scheduleStickerSummary.scheduleStickers.map { it.name }
	}

	@DgsData(parentType = "Schedule")
	fun status(dfe: DgsDataFetchingEnvironment): Status {
		val args: Map<String, LocalDateTime> = dfe.getLocalContext()
		val targetSchedule = dfe.getSource<Schedule>()
		val date = args["date"]!!
		val status = scheduleService.getScheduleStatus(targetSchedule.id.toLong(), date)
		val endDateTime = date.toLocalDate().atTime(targetSchedule.dateTimeEnd.toLocalTime())
		if (LocalDateTime.now().isBefore(endDateTime)) {
			return Status.NOT_YET
		}
		return status
	}
}
