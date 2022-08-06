package com.yapp.weekand.domain.sticker.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.sticker.service.ScheduleStickerService
import java.time.LocalDateTime

@DgsComponent
class ScheduleStickerQueryResolver(
	private val scheduleStickerService: ScheduleStickerService
) {
	@DgsQuery
	@JwtAuth
	fun scheduleStickerSummary(@InputArgument scheduleId: String, @InputArgument selectedDate: LocalDateTime) =
		scheduleStickerService.getScheduleStickerSummary(scheduleId = scheduleId.toLong(), selectedDate)
}
