package com.yapp.weekand.domain.sticker.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.domain.sticker.service.ScheduleStickerService
import org.springframework.security.access.prepost.PreAuthorize
import java.time.LocalDateTime

@DgsComponent
class ScheduleStickerQueryResolver(
	private val scheduleStickerService: ScheduleStickerService
) {
	@DgsQuery
	@PreAuthorize("isAuthenticated()")
	fun scheduleStickerSummary(@InputArgument scheduleId: String, @InputArgument selectedDate: LocalDateTime) =
		scheduleStickerService.getScheduleStickerSummary(scheduleId = scheduleId.toLong(), selectedDate)
}
