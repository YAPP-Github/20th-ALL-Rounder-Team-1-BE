package com.yapp.weekand.domain.schedule.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.ScheduleInput
import com.yapp.weekand.domain.schedule.entity.RepeatType
import com.yapp.weekand.domain.schedule.exception.ScheduleInvalidDateException
import com.yapp.weekand.domain.schedule.exception.ScheduleRepeatValueInvalidException
import com.yapp.weekand.domain.schedule.service.ScheduleService
import com.yapp.weekand.domain.user.service.UserService
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class ScheduleMutationResolver (
	private val scheduleService: ScheduleService,
	private val userService: UserService
) {
	@DgsMutation
	@PreAuthorize("isAuthenticated()")
	fun createSchedule(@InputArgument input: ScheduleInput): Boolean {
		if (input.dateTimeStart >= input.dateTimeEnd) {
			throw ScheduleInvalidDateException()
		}

		if (input.repeatType == RepeatType.WEEKLY && input.repeatSelectedValue == null) {
			throw ScheduleRepeatValueInvalidException()
		}

		scheduleService.createSchedule(input, userService.getCurrentUser())
		return true
	}
}
