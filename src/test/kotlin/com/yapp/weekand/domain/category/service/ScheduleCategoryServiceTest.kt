package com.yapp.weekand.domain.category.service

import com.yapp.weekand.api.generated.types.ScheduleCategorySort
import com.yapp.weekand.common.entity.EntityFactory
import com.yapp.weekand.domain.category.entity.ScheduleCategory
import com.yapp.weekand.domain.category.entity.ScheduleCategoryOpenType
import com.yapp.weekand.domain.category.repository.ScheduleCategoryRepository
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import org.springframework.data.domain.Sort
import com.yapp.weekand.api.generated.types.ScheduleCategory as ScheduleCategoryGraphqlType

@ExtendWith(MockKExtension::class)
internal class ScheduleCategoryServiceTest {

	@InjectMockKs
	lateinit var scheduleCategoryService: ScheduleCategoryService

	@MockK(relaxed = true)
	lateinit var scheduleCategoryRepository: ScheduleCategoryRepository

	@MockK(relaxed = true)
	lateinit var scheduleRepository: ScheduleRepository

	@Test
	fun `일정 카테고리 목록을 조회해야 한다`() {
		val givenUser = EntityFactory.testLoginUser()
		val givenSort = ScheduleCategorySort.NAME_DESC
		var givenPage = 1
		var givenSize = 5

		scheduleCategoryService.getScheduleCategories(user = givenUser, size = givenSize, page = givenPage, sort = givenSort)

		val expectedSort = Sort.by("name").descending()
		val expectedPageable = PageRequest.of(givenPage, givenSize, expectedSort)

		verify(exactly = 1) {
			scheduleCategoryRepository.findByUser(givenUser, expectedPageable)
		}
	}

	@Test
	fun `일정 카테고리 목록을 반환해야 한다`() {
		val givenUser = EntityFactory.testLoginUser()
		val givenSort = ScheduleCategorySort.NAME_DESC
		var givenPage = 1
		var givenSize = 5


		val givenScheduleCategories = SliceImpl(
			listOf(
				ScheduleCategory(
					id = 1,
					user = givenUser,
					name = "카테고리 1",
					color = "red",
					openType = ScheduleCategoryOpenType.ALL_OPEN
				),
				ScheduleCategory(
					id = 2,
					user = givenUser,
					name = "카테고리 2",
					color = "blue",
					openType = ScheduleCategoryOpenType.FOLLOWER_OPEN
				),
			)
		)

		val expectedSort = Sort.by("name").descending()
		val expectedPageable = PageRequest.of(givenPage, givenSize, expectedSort)

		every {
			scheduleCategoryRepository.findByUser(
				user = givenUser,
				expectedPageable
			)
		} returns givenScheduleCategories

		val result = scheduleCategoryService.getScheduleCategories(
			user = givenUser,
			size = givenSize,
			page = givenPage,
			sort = givenSort,
		)

		val expectedScheduleCategories = givenScheduleCategories.map {
			ScheduleCategoryGraphqlType(
				id = it.id.toString(),
				name = it.name,
				color = it.color,
				openType = it.openType
			)
		}

		Assertions.assertEquals(result, expectedScheduleCategories)
	}
}
