package com.yapp.weekand.domain.sticker.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.domain.sticker.service.ScheduleStickerService
import java.time.LocalDateTime

@DgsComponent
class ScheduleStickerResolver(
	private val scheduleStickerService: ScheduleStickerService
) {
	@DgsQuery
	fun scheduleStickerSummary(@InputArgument scheduleId: String, @InputArgument selectedDate: LocalDateTime) =
		scheduleStickerService.getScheduleStickerSummary(scheduleId = scheduleId.toLong(), selectedDate)
}
