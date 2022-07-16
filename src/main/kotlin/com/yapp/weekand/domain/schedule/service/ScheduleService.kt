package com.yapp.weekand.domain.schedule.service

import com.yapp.weekand.api.generated.types.ScheduleInfo
import com.yapp.weekand.api.generated.types.ScheduleInput
import com.yapp.weekand.api.generated.types.UpdateScheduleInput
import com.yapp.weekand.domain.auth.exception.UnauthorizedAccessException
import com.yapp.weekand.domain.category.exception.ScheduleCategoryNotFoundException
import com.yapp.weekand.domain.category.repository.ScheduleCategoryRepository
import com.yapp.weekand.domain.schedule.entity.RepeatType
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.entity.ScheduleStatus
import com.yapp.weekand.domain.schedule.exception.ScheduleDuplicatedStatusException
import com.yapp.weekand.domain.schedule.exception.ScheduleInvalidSkipDateException
import com.yapp.weekand.domain.schedule.exception.ScheduleNotFoundException
import com.yapp.weekand.domain.schedule.mapper.toGraphql
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import com.yapp.weekand.domain.schedule.repository.ScheduleStatusRepository
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class ScheduleService(
	private val scheduleRepository: ScheduleRepository,
	private val scheduleCategoryRepository: ScheduleCategoryRepository,
	private val scheduleStatusRepository: ScheduleStatusRepository
) {
	fun getSchedule(scheduleId: Long): ScheduleInfo {
		val schedule = scheduleRepository.findByScheduleId(scheduleId)
			?: throw ScheduleNotFoundException()
		return schedule.toGraphql()
	}

	@Transactional
	fun createSchedule(schedule: ScheduleInput, user: User) {
		val category = scheduleCategoryRepository.findByIdOrNull(schedule.categoryId.toLong())
			?: throw ScheduleCategoryNotFoundException()

		if (category.user.id != user.id) {
			throw UnauthorizedAccessException()
		}

		scheduleRepository.save(ScheduleRule.of(schedule, category, user))
	}

	@Transactional
	fun skipSchedule(scheduleId: Long, skipDate: LocalDateTime, user: User) {
		val schedule = scheduleRepository.findByIdOrNull(scheduleId)
			?: throw ScheduleNotFoundException()

		if (user.id != schedule.user.id) {
			throw UnauthorizedAccessException()
		}

		if (schedule.dateRepeatEnd != null && skipDate > schedule.dateRepeatEnd) {
			throw ScheduleInvalidSkipDateException()
		}

		if (scheduleStatusRepository.existsByDateYmdAndScheduleRule(skipDate.toLocalDate(), schedule)) {
			throw ScheduleDuplicatedStatusException()
		}
		scheduleStatusRepository.save(ScheduleStatus.skipSchedule(skipDate.toLocalDate(), schedule))
	}

	@Transactional
	fun deleteSchedule(scheduleId: Long, user: User) {
		val schedule = scheduleRepository.findByIdOrNull(scheduleId)
			?: throw ScheduleNotFoundException()

		if (user.id != schedule.user.id) {
			throw UnauthorizedAccessException()
		}

		scheduleRepository.delete(schedule)
	}

	@Transactional
	fun updateSchedule(input: UpdateScheduleInput, user: User) {
		val existedSchedule = scheduleRepository.findByIdOrNull(input.id.toLong()) ?: throw ScheduleNotFoundException()

		val category = scheduleCategoryRepository.findByIdOrNull(input.categoryId.toLong())
			?: throw ScheduleCategoryNotFoundException()

		if (checkRequiredNewSchedule(input, existedSchedule)) {
			scheduleRepository.save(
				ScheduleRule.of(
					ScheduleInput(
						name = input.name,
						categoryId = input.categoryId,
						dateTimeStart = input.dateTimeStart,
						dateTimeEnd = input.dateTimeEnd,
						repeatEnd = input.repeatEnd,
						repeatType = input.repeatType,
						memo = input.memo,
						repeatSelectedValue = if (input.repeatType == RepeatType.WEEKLY) input.repeatSelectedValue else listOf()
					), category, user
				)
			)
			existedSchedule.dateRepeatEnd = input.dateTimeStart
		} else {
			scheduleRepository.save(ScheduleRule.of(input, category, user))
		}
	}

	private fun checkRequiredNewSchedule(updateInput: UpdateScheduleInput, existedSchedule: ScheduleRule): Boolean {
		if (updateInput.dateTimeStart != existedSchedule.dateStart) {
			return true
		}
		if (updateInput.dateTimeEnd != existedSchedule.dateEnd) {
			return true
		}
		if (updateInput.repeatType != existedSchedule.repeatType) {
			return true
		}

		val existedRepeatSelectedValue = existedSchedule.repeatSelectedValue?.split(",")?.sorted()
		val inputRepeatSelectedValue = updateInput.repeatSelectedValue?.map { it.toString() }?.sorted()
		if (existedRepeatSelectedValue != inputRepeatSelectedValue) {
			return true
		}

		return false
	}
}
