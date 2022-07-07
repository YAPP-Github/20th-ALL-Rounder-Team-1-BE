package com.yapp.weekand.domain.schedule.service

import com.yapp.weekand.api.generated.types.ScheduleInfo
import com.yapp.weekand.api.generated.types.ScheduleInput
import com.yapp.weekand.domain.auth.exception.UnauthorizedAccessException
import com.yapp.weekand.domain.category.exception.ScheduleCategoryNotFoundException
import com.yapp.weekand.domain.category.repository.ScheduleCategoryRepository
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.exception.ScheduleNotFoundException
import com.yapp.weekand.domain.schedule.mapper.toGraphql
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ScheduleService (
	private val scheduleRepository: ScheduleRepository,
	private val scheduleCategoryRepository: ScheduleCategoryRepository
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
}
