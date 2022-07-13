package com.yapp.weekand.domain.schedule.service

import com.yapp.weekand.api.generated.types.ScheduleInfo
import com.yapp.weekand.api.generated.types.ScheduleInput
import com.yapp.weekand.domain.auth.exception.UnauthorizedAccessException
import com.yapp.weekand.domain.category.exception.ScheduleCategoryNotFoundException
import com.yapp.weekand.domain.category.repository.ScheduleCategoryRepository
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
class ScheduleService (
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
}
