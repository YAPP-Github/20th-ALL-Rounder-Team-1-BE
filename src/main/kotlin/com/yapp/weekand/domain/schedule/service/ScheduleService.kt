package com.yapp.weekand.domain.schedule.service

import com.yapp.weekand.api.generated.types.*
import com.yapp.weekand.domain.auth.exception.UnauthorizedAccessException
import com.yapp.weekand.domain.category.exception.ScheduleCategoryNotFoundException
import com.yapp.weekand.domain.category.repository.ScheduleCategoryRepository
import com.yapp.weekand.domain.schedule.entity.RepeatType
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.entity.ScheduleStatus
import com.yapp.weekand.domain.schedule.entity.Status
import com.yapp.weekand.domain.schedule.exception.ScheduleNotFoundException
import com.yapp.weekand.domain.schedule.exception.ScheduleStatusInvalidDateException
import com.yapp.weekand.domain.schedule.mapper.toScheduleInfoGraphql
import com.yapp.weekand.domain.schedule.mapper.toScheduleRuleGraphql
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import com.yapp.weekand.domain.schedule.repository.ScheduleStatusRepository
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import com.yapp.weekand.api.generated.types.ScheduleRule as ScheduleRuleGraphql
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class ScheduleService(
	private val scheduleRepository: ScheduleRepository,
	private val scheduleCategoryRepository: ScheduleCategoryRepository,
	private val scheduleStatusRepository: ScheduleStatusRepository
) {
	fun getScheduleRule(scheduleId: Long): ScheduleRuleGraphql {
		val schedule = scheduleRepository.findByScheduleId(scheduleId)
			?: throw ScheduleNotFoundException()
		return schedule.toScheduleRuleGraphql()
	}

	fun getSchedule(scheduleId: Long, date: LocalDate): ScheduleInfo {
		val schedule = scheduleRepository.findByScheduleId(scheduleId)
			?: throw ScheduleNotFoundException()

		val startTime = schedule.dateStart.toLocalTime()
		val endTime = schedule.dateEnd.toLocalTime()

		return schedule.toScheduleInfoGraphql(
			dateTimeStart = date.atTime(startTime),
			dateTimeEnd = date.atTime(endTime)
		)
	}

	fun getScheduleStatus(scheduleId: Long, date: LocalDateTime): Status {
		val scheduleStatus = scheduleStatusRepository.findByDateYmdAndScheduleRuleId(date.toLocalDate(), scheduleId)
			?: return Status.UNDETERMINED
		return scheduleStatus.status
	}


	@Transactional
	fun createSchedule(schedule: ScheduleInput, user: User) {
		val category = scheduleCategoryRepository.findByIdOrNull(schedule.categoryId.toLong())
			?: throw ScheduleCategoryNotFoundException()

		if (category.user.id != user.id) {
			throw UnauthorizedAccessException()
		}

		val scheduleInput = calibrateScheduleRuleInputDate(schedule)

		scheduleRepository.save(ScheduleRule.of(scheduleInput, category, user))
	}

	fun calibrateScheduleRuleInputDate(scheduleInput: ScheduleInput): ScheduleInput {
		if (scheduleInput.repeatType != RepeatType.WEEKLY) {
			return scheduleInput
		}

		if (scheduleInput.repeatSelectedValue.isEmpty()) {
			return scheduleInput
		}

		var calibratedDateTimeStart = scheduleInput.dateTimeStart
		var calibratedDateTimeEnd = scheduleInput.dateTimeEnd

		do {
			val targetDateDayOfWeek = when (calibratedDateTimeStart.dayOfWeek) {
				DayOfWeek.MONDAY -> Week.MONDAY
				DayOfWeek.TUESDAY -> Week.TUESDAY
				DayOfWeek.WEDNESDAY -> Week.WEDNESDAY
				DayOfWeek.THURSDAY -> Week.THURSDAY
				DayOfWeek.FRIDAY -> Week.FRIDAY
				DayOfWeek.SATURDAY -> Week.SATURDAY
				DayOfWeek.SUNDAY -> Week.SUNDAY
			}

			if (scheduleInput.repeatSelectedValue.contains(targetDateDayOfWeek)) {
				break
			}

			calibratedDateTimeStart = calibratedDateTimeStart.plusDays(1)
			calibratedDateTimeEnd = calibratedDateTimeEnd.plusDays(1)
		} while (true)

		return ScheduleInput(
			name = scheduleInput.name,
			categoryId = scheduleInput.categoryId,
			dateTimeStart = calibratedDateTimeStart,
			dateTimeEnd = calibratedDateTimeEnd,
			repeatType = scheduleInput.repeatType,
			repeatSelectedValue = scheduleInput.repeatSelectedValue,
			repeatEnd = scheduleInput.repeatEnd,
			memo = scheduleInput.memo
		)
	}

	@Transactional
	fun updateScheduleStatus(input: ScheduleStateInput, user: User, status: Status) {
		val schedule = scheduleRepository.findByIdOrNull(input.scheduleId.toLong())
			?: throw ScheduleNotFoundException()

		if (user.id != schedule.user.id) {
			throw UnauthorizedAccessException()
		}

		if (!validDate(status, schedule, input.date)) {
			throw ScheduleStatusInvalidDateException()
		}

		scheduleStatusRepository.deleteByDateYmdAndScheduleRule(input.date.toLocalDate(), schedule)
		scheduleStatusRepository.save(ScheduleStatus.of(status, input.date.toLocalDate(), schedule))
	}

	private fun validDate (status: Status, schedule: ScheduleRule, date: LocalDateTime): Boolean {
		if (schedule.dateRepeatEnd != null && date > schedule.dateRepeatEnd) {
			return false
		}
		if ((status == Status.COMPLETED) or (status == Status.INCOMPLETED)) {
			if (LocalDateTime.now().isBefore(date.toLocalDate().atTime(schedule.dateEnd.toLocalTime()))) {
				return false
			}
		}
		return true
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
	fun deleteScheduleFromDate(input: DeleteScheduleFromDateInput, user: User) {
		val schedule = scheduleRepository.findByIdOrNull(input.scheduleId.toLong())
			?: throw ScheduleNotFoundException()

		if (user.id != schedule.user.id) {
			throw UnauthorizedAccessException()
		}

		schedule.updateDateRepeatEnd(input.date.minusDays(1))
	}

	@Transactional
	fun updateSchedule(input: UpdateScheduleInput, user: User) {
		val existedSchedule = scheduleRepository.findByIdOrNull(input.id.toLong()) ?: throw ScheduleNotFoundException()

		val category = scheduleCategoryRepository.findByIdOrNull(input.categoryId.toLong())
			?: throw ScheduleCategoryNotFoundException()

		if (existedSchedule.repeatType === RepeatType.ONCE) {
			scheduleRepository.save(ScheduleRule.of(input, category, user))
			return
		}

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
			existedSchedule.dateRepeatEnd = input.requestDateTime.minusDays(1)
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

		val existedRepeatSelectedValue =
			existedSchedule.repeatSelectedValue.split(",").filter { it.isNotEmpty() }.sorted()
		val inputRepeatSelectedValue = updateInput.repeatSelectedValue.map { it.toString() }.sorted()
		if (existedRepeatSelectedValue != inputRepeatSelectedValue) {
			return true
		}

		return false
	}

	@Transactional
	fun deleteScheduleByUser(user: User) {
		val schedules = scheduleRepository.findByUser(user)
		scheduleRepository.deleteAllInBatch(schedules)
		scheduleStatusRepository.deleteAllInBatch(schedules.flatMap { it.scheduleStatus })
	}

	fun getUserSchedulesByDate(date: LocalDateTime, userId: Long, currentUserId: Long): List<ScheduleRule> {
		val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
		return scheduleRepository.getUserSchedulesByDate(date.format(formatter), userId, currentUserId)
	}
}
