package com.yapp.weekand.domain.sticker.service

import com.yapp.weekand.api.generated.types.ScheduleSticker
import com.yapp.weekand.api.generated.types.ScheduleStickerSummary
import com.yapp.weekand.api.generated.types.ScheduleStickerUser
import com.yapp.weekand.common.entity.ScheduleRuleFactory
import com.yapp.weekand.common.entity.ScheduleStickerFactory
import com.yapp.weekand.common.entity.UserFactory
import com.yapp.weekand.domain.schedule.exception.ScheduleNotFoundException
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import com.yapp.weekand.domain.sticker.entity.ScheduleStickerName
import com.yapp.weekand.domain.sticker.repository.ScheduleStickerRepository
import com.yapp.weekand.domain.user.mapper.toGraphql
import com.yapp.weekand.domain.user.service.UserService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class ScheduleStickerServiceTest {

	private val TEST_LOCAL_DATE_TIME: LocalDateTime = LocalDateTime.of(2022, 6, 12, 5, 30)

	@InjectMockKs
	lateinit var scheduleStickerService: ScheduleStickerService

	@MockK(relaxed = true)
	lateinit var scheduleStickerRepository: ScheduleStickerRepository

	@MockK(relaxed = true)
	lateinit var scheduleRepository: ScheduleRepository

	@MockK(relaxed = true)
	lateinit var userService: UserService

	@Test
	fun `스케줄을 조회해야 한다`() {
		val givenScheduleId = 1L

		every {
			scheduleRepository.findByIdOrNull(givenScheduleId)
		} returns ScheduleRuleFactory.scheduleRule()

		scheduleStickerService.getScheduleStickerSummary(givenScheduleId, TEST_LOCAL_DATE_TIME)

		verify(exactly = 1) {
			scheduleRepository.findByIdOrNull(givenScheduleId)
		}
	}

	@Test
	fun `스케줄이 존재하지 않으면 에러를 반환해야 한다`() {
		val givenScheduleId = 1L

		every {
			scheduleRepository.findByIdOrNull(givenScheduleId)
		} returns null


		assertThrows<ScheduleNotFoundException> {
			scheduleStickerService.getScheduleStickerSummary(
				givenScheduleId,
				TEST_LOCAL_DATE_TIME
			)
		}
	}

	@Test
	fun `스케줄 스티커를 조회해야 한다`() {
		val givenScheduleId = 1L
		val givenScheduleRule = ScheduleRuleFactory.scheduleRule()

		every {
			scheduleRepository.findByIdOrNull(givenScheduleId)
		} returns givenScheduleRule

		scheduleStickerService.getScheduleStickerSummary(givenScheduleId, TEST_LOCAL_DATE_TIME)

		val expectedLocalDate = TEST_LOCAL_DATE_TIME.toLocalDate()

		verify(exactly = 1) {
			scheduleStickerRepository.findByScheduleRuleAndScheduleDateOrderByDateCreatedDesc(
				givenScheduleRule,
				expectedLocalDate
			)
		}
	}

	@Test
	fun `스케줄 스티커 요약을 반환해야 한다`() {
		val givenScheduleId = 1L
		val givenScheduleRule = ScheduleRuleFactory.scheduleRule()
		val givenScheduleStickerList = ScheduleStickerFactory.scheduleStickerList(3)

		val expectedLocalDate = TEST_LOCAL_DATE_TIME.toLocalDate()

		every {
			scheduleRepository.findByIdOrNull(givenScheduleId)
		} returns givenScheduleRule

		every {
			scheduleStickerRepository.findByScheduleRuleAndScheduleDateOrderByDateCreatedDesc(
				givenScheduleRule,
				expectedLocalDate
			)
		} returns givenScheduleStickerList

		val scheduleStickers = givenScheduleStickerList
			.groupingBy { it.name }
			.eachCount()
			.map {
				ScheduleSticker(
					name = it.key,
					stickerCount = it.value
				)
			}

		val scheduleStickerUsers = givenScheduleStickerList
			.map {
				ScheduleStickerUser(
					user = it.user.toGraphql(),
					stickerName = it.name,
				)
			}

		val expected_result = ScheduleStickerSummary(
			totalCount = givenScheduleStickerList.size,
			scheduleStickers,
			scheduleStickerUsers,
		)

		val result = scheduleStickerService.getScheduleStickerSummary(givenScheduleId, TEST_LOCAL_DATE_TIME)

		Assertions.assertEquals(expected_result, result)
	}

	@Test
	fun `스티커를 등록한다`() {
		val givenUser = UserFactory.testLoginUser()
		val scheduleStickerInput = ScheduleStickerFactory.scheduleStickerInput()
		val schedule = ScheduleRuleFactory.scheduleRule()

		every { scheduleRepository.findByIdOrNull(any()) } returns schedule
		every { scheduleStickerRepository.findByUserAndScheduleRuleAndScheduleDate(any(), any(), any()) } returns null
		every { scheduleStickerRepository.save(any()) } returns ScheduleStickerFactory.scheduleSticker(schedule, ScheduleStickerName.CHEER_UP)

		scheduleStickerService.createScheduleSticker(scheduleStickerInput, givenUser)

		verify(exactly = 1) {
			scheduleStickerRepository.save(any())
		}
	}

	@Test
	fun `다른 스티커가 있을 시 삭제하고 등록한다`() {
		val givenUser = UserFactory.testLoginUser()
		val scheduleStickerInput = ScheduleStickerFactory.scheduleStickerInput()
		val schedule = ScheduleRuleFactory.scheduleRule()
		val oldScheduleSticker = ScheduleStickerFactory.scheduleSticker(schedule, ScheduleStickerName.COOL)

		every { scheduleRepository.findByIdOrNull(any()) } returns schedule
		every { scheduleStickerRepository.findByUserAndScheduleRuleAndScheduleDate(any(), any(), any()) } returns oldScheduleSticker
		every { scheduleStickerRepository.save(any()) } returns ScheduleStickerFactory.scheduleSticker(schedule, ScheduleStickerName.CHEER_UP)

		scheduleStickerService.createScheduleSticker(scheduleStickerInput, givenUser)

		verify(exactly = 1) {
			scheduleStickerRepository.delete(any())
			scheduleStickerRepository.save(any())
		}
	}

	@Test
	fun `존재하지 않는 스케줄이라면 예외 발생`() {
		val givenUser = UserFactory.testLoginUser()
		val scheduleStickerInput = ScheduleStickerFactory.scheduleStickerInput()
		every { scheduleRepository.findByIdOrNull(any()) } returns null

		assertThrows<ScheduleNotFoundException> {
			scheduleStickerService.createScheduleSticker(scheduleStickerInput, givenUser)
		}
	}
}
