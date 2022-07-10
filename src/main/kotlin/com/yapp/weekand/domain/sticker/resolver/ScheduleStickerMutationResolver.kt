package com.yapp.weekand.domain.sticker.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.CreateScheduleStickerInput
import com.yapp.weekand.api.generated.types.DeleteScheduleStickerInput
import com.yapp.weekand.domain.sticker.service.ScheduleStickerService
import com.yapp.weekand.domain.user.service.UserService
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class ScheduleStickerMutationResolver(
	private val scheduleStickerService: ScheduleStickerService,
	private val userService: UserService
) {
	@DgsMutation
	@PreAuthorize("isAuthenticated()")
	fun createScheduleSticker(@InputArgument input: CreateScheduleStickerInput): Boolean {
		scheduleStickerService.createScheduleSticker(input, userService.getCurrentUser())
		return true
	}

	@DgsMutation
	@PreAuthorize("isAuthenticated()")
	fun deleteScheduleSticker(@InputArgument input: DeleteScheduleStickerInput): Boolean {
		scheduleStickerService.deleteScheduleSticker(input)
		return true
	}
}
