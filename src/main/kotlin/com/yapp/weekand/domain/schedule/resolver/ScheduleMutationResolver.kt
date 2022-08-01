package com.yapp.weekand.domain.schedule.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.*
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.schedule.entity.RepeatType
import com.yapp.weekand.domain.schedule.entity.Status
import com.yapp.weekand.domain.schedule.exception.ScheduleInvalidDateException
import com.yapp.weekand.domain.schedule.service.ScheduleService
import com.yapp.weekand.domain.user.service.UserService
import com.yapp.weekand.domain.schedule.exception.ScheduleRepeatValueInvalidException

@DgsComponent
class ScheduleMutationResolver(
	private val scheduleService: ScheduleService,
	private val userService: UserService
) {
	@DgsMutation
	@JwtAuth
	fun createSchedule(@InputArgument input: ScheduleInput): Boolean {
		if (input.dateTimeStart > input.dateTimeEnd) {
			throw ScheduleInvalidDateException()
		}

		if (input.repeatType == RepeatType.WEEKLY && input.repeatSelectedValue.isNullOrEmpty()) {
			throw ScheduleRepeatValueInvalidException()
		}

		scheduleService.createSchedule(input, userService.getCurrentUser())
		return true
	}

	@DgsMutation
	@JwtAuth
	fun updateSchedule(@InputArgument input: UpdateScheduleInput): Boolean {
		if (input.dateTimeStart > input.dateTimeEnd) {
			throw ScheduleInvalidDateException()
		}

		if (input.repeatType == RepeatType.WEEKLY && input.repeatSelectedValue.isNullOrEmpty()) {
			throw ScheduleRepeatValueInvalidException()
		}

		scheduleService.updateSchedule(input, userService.getCurrentUser())
		return true
	}

	@DgsMutation
	@JwtAuth
	fun skipSchedule(@InputArgument input: ScheduleStateInput): Boolean {
		scheduleService.updateScheduleStatus(input, userService.getCurrentUser(), Status.SKIP)
		return true
	}

	@DgsMutation
	@JwtAuth
	fun deleteSchedule(@InputArgument input: DeleteScheduleInput): Boolean {
		scheduleService.deleteSchedule(input.scheduleId.toLong(), userService.getCurrentUser())
		return true
	}

	@DgsMutation
	@JwtAuth
	fun deleteScheduleFromDate(@InputArgument input: DeleteScheduleFromDateInput): Boolean {
		scheduleService.deleteScheduleFromDate(input, userService.getCurrentUser())
		return true
	}

	@DgsMutation
	@JwtAuth
	fun completeSchedule(@InputArgument input: ScheduleStateInput): Boolean {
		scheduleService.updateScheduleStatus(input, userService.getCurrentUser(), Status.COMPLETED)
		return true
	}

	@DgsMutation
	@JwtAuth
	fun incompleteSchedule(@InputArgument input: ScheduleStateInput): Boolean {
		scheduleService.updateScheduleStatus(input, userService.getCurrentUser(), Status.INCOMPLETED)
		return true
	}
}
