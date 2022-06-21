package com.yapp.weekand.domain.sticker.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.domain.sticker.service.ScheduleStickerService

@DgsComponent
class ScheduleStickerResolver(
	private val scheduleStickerService: ScheduleStickerService
) {
	@DgsQuery
	fun scheduleStickerSummary(@InputArgument scheduleId: String) = scheduleStickerService.getScheduleStickerSummary(scheduleId = scheduleId.toLong())
}
