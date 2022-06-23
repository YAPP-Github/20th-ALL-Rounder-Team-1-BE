package com.yapp.weekand.domain.sticker.service

import com.yapp.weekand.api.generated.types.ScheduleSticker
import com.yapp.weekand.api.generated.types.ScheduleStickerSummary
import com.yapp.weekand.api.generated.types.ScheduleStickerUser
import com.yapp.weekand.common.entity.ScheduleRuleFactory
import com.yapp.weekand.common.entity.ScheduleStickerFactory
import com.yapp.weekand.domain.schedule.exception.ScheduleNotFoundException
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import com.yapp.weekand.domain.sticker.repository.ScheduleStickerRepository
import com.yapp.weekand.domain.user.mapper.toGraphql
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
internal class ScheduleStickerServiceTest {

	private val TEST_LOCAL_DATE: LocalDate = LocalDate.of(2022, 6, 12)

	@InjectMockKs
	lateinit var scheduleStickerService: ScheduleStickerService

	@MockK(relaxed = true)
	lateinit var scheduleStickerRepository: ScheduleStickerRepository

	@MockK(relaxed = true)
	lateinit var scheduleRepository: ScheduleRepository

	@Test
	fun `스케줄을 조회해야 한다`() {
		val givenScheduleId = 1L

		every {
			scheduleRepository.findByIdOrNull(givenScheduleId)
		} returns ScheduleRuleFactory.scheduleRule()

		scheduleStickerService.getScheduleStickerSummary(givenScheduleId)

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


		assertThrows<ScheduleNotFoundException> { scheduleStickerService.getScheduleStickerSummary(givenScheduleId) }
	}

	@Test
	fun `스케줄 스티커를 조회해야 한다`() {
		val givenScheduleId = 1L
		val givenScheduleRule = ScheduleRuleFactory.scheduleRule()

		mockkStatic(LocalDate::class)
		every { LocalDate.now() } returns TEST_LOCAL_DATE

		every {
			scheduleRepository.findByIdOrNull(givenScheduleId)
		} returns givenScheduleRule

		scheduleStickerService.getScheduleStickerSummary(givenScheduleId)

		verify(exactly = 1) {
			scheduleStickerRepository.findByScheduleRuleAndScheduleDateOrderByDateCreatedDesc(givenScheduleRule, TEST_LOCAL_DATE)
		}
	}

	@Test
	fun `스케줄 스티커 요약을 반환해야 한다`() {
		val givenScheduleId = 1L
		val givenScheduleRule = ScheduleRuleFactory.scheduleRule()
		val givenScheduleStickerList = ScheduleStickerFactory.scheduleStickerList(3)

		mockkStatic(LocalDate::class)
		every { LocalDate.now() } returns TEST_LOCAL_DATE

		every {
			scheduleRepository.findByIdOrNull(givenScheduleId)
		} returns givenScheduleRule

		every {
			scheduleStickerRepository.findByScheduleRuleAndScheduleDateOrderByDateCreatedDesc(givenScheduleRule, TEST_LOCAL_DATE)
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

		val result = scheduleStickerService.getScheduleStickerSummary(givenScheduleId)

		Assertions.assertEquals(expected_result, result)
	}
}
