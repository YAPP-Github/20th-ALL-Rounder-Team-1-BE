package com.yapp.weekand.domain.schedule.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.yapp.weekand.api.generated.types.ScheduleInfo
import com.yapp.weekand.domain.schedule.entity.Status
import com.yapp.weekand.domain.schedule.service.ScheduleService
import java.time.LocalDateTime

@DgsComponent
class ScheduleInfoResolver(
	private val scheduleService: ScheduleService
) {
	@DgsData(parentType = "ScheduleInfo")
	fun status(dfe: DgsDataFetchingEnvironment): Status {
		val args: Map<String, LocalDateTime> = dfe.getLocalContext()
		val date = args["date"]!!
		val targetSchedule = dfe.getSource<ScheduleInfo>()
		val status = scheduleService.getScheduleStatus(targetSchedule.id.toLong(), date)
		if(LocalDateTime.now().isBefore(targetSchedule.dateTimeStart)) {
			return Status.NOT_YET
		}
		return status
	}
}
