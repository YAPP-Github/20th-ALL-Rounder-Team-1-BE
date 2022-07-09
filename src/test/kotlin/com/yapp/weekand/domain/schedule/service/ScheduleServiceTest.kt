package com.yapp.weekand.domain.schedule.service

import com.yapp.weekand.common.entity.ScheduleCategoryFactory
import com.yapp.weekand.common.entity.ScheduleRuleFactory
import com.yapp.weekand.common.entity.UserFactory
import com.yapp.weekand.domain.auth.exception.UnauthorizedAccessException
import com.yapp.weekand.domain.category.exception.ScheduleCategoryNotFoundException
import com.yapp.weekand.domain.category.repository.ScheduleCategoryRepository
import com.yapp.weekand.domain.category.service.ScheduleCategoryService
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import com.yapp.weekand.domain.schedule.repository.ScheduleStatusRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull

@ExtendWith(MockKExtension::class)
class ScheduleServiceTest {

	@InjectMockKs
	lateinit var scheduleService: ScheduleService

	@MockK(relaxed = true)
	lateinit var scheduleRepository: ScheduleRepository

	@MockK(relaxed = true)
	lateinit var scheduleCategoryService: ScheduleCategoryService

	@MockK(relaxed = true)
	lateinit var scheduleCategoryRepository: ScheduleCategoryRepository

	@MockK(relaxed = true)
	lateinit var scheduleStatusRepository: ScheduleStatusRepository

	@Test
	fun `일정을 추가한다`() {
		val givenUser = UserFactory.testLoginUser()
		val schedule = ScheduleRuleFactory.scheduleInput()
		val category = ScheduleCategoryFactory.category(givenUser)

		every { scheduleCategoryRepository.findByIdOrNull(any()) } returns category
		every { scheduleRepository.save(any()) } returns ScheduleRule.of(schedule, category, givenUser)

		scheduleService.createSchedule(schedule, givenUser)

		verify(exactly = 1) {
			scheduleRepository.save(any())
		}
	}

	@Test
	fun `일정 추가 시 존재하지 않는 카테고리라면 예외 발생`() {
		val givenUser = UserFactory.testLoginUser()
		val schedule = ScheduleRuleFactory.scheduleInput()
		every { scheduleCategoryRepository.findByIdOrNull(any()) } returns null

		assertThrows<ScheduleCategoryNotFoundException> {
			scheduleService.createSchedule(schedule, givenUser)
		}
	}

	@Test
	fun `일정 추가 시 카테고리의 주인이 아니라면 예외 발생`() {
		val givenUser = UserFactory.testLoginUser()
		val schedule = ScheduleRuleFactory.scheduleInput()
		val otherUser = UserFactory.testUser()
		val category = ScheduleCategoryFactory.category(givenUser)
		every { scheduleCategoryRepository.findByIdOrNull(any()) } returns category

		assertThrows<UnauthorizedAccessException> {
			scheduleService.createSchedule(schedule, otherUser)
		}
	}
 }
