package com.yapp.weekand.domain.category.service

import com.yapp.weekand.api.generated.types.ScheduleCategorySort
import com.yapp.weekand.common.entity.ScheduleCategoryFactory
import com.yapp.weekand.common.entity.UserFactory
import com.yapp.weekand.domain.auth.exception.UnauthorizedAccessException
import com.yapp.weekand.domain.category.entity.ScheduleCategory
import com.yapp.weekand.domain.category.entity.ScheduleCategoryOpenType
import com.yapp.weekand.domain.category.exception.ScheduleCategoryNotFoundException
import com.yapp.weekand.domain.category.repository.ScheduleCategoryRepository
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
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
		val givenUser = UserFactory.testLoginUser()
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
		val givenUser = UserFactory.testLoginUser()
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

	@Test
	fun `카테고리를 추가한다`() {
		val givenUser = UserFactory.testLoginUser()
		val category = ScheduleCategoryFactory.categoryInput()
		every { scheduleCategoryRepository.save(any()) } returns ScheduleCategory.of(category, givenUser)

		scheduleCategoryService.createCategory(category, givenUser)

		verify(exactly = 1) {
			scheduleCategoryRepository.save(any())
		}
	}

	@Test
	fun `카테고리를 수정한다`() {
		val givenUser = UserFactory.testLoginUser()
		val category = ScheduleCategoryFactory.category(givenUser)
		val updateCategory = ScheduleCategoryFactory.categoryUpdateInput()
		every { scheduleCategoryRepository.findByIdOrNull(any()) } returns category

		scheduleCategoryService.updateCategory(category.id!!, updateCategory, givenUser)

		assertThat(category.name).isEqualTo(updateCategory.name)
		assertThat(category.color).isEqualTo(updateCategory.color)
		assertThat(category.openType).isEqualTo(updateCategory.openType)
	}

	@Test
	fun `카테고리 수정시 존재하지 않는 카테고리라면 예외`() {
		val givenUser = UserFactory.testLoginUser()
		val category = ScheduleCategoryFactory.category(givenUser)
		val updateCategory = ScheduleCategoryFactory.categoryUpdateInput()
		every { scheduleCategoryRepository.findByIdOrNull(any()) } returns null

		assertThrows<ScheduleCategoryNotFoundException> {
			scheduleCategoryService.updateCategory(category.id!!, updateCategory, givenUser)
		}
	}

	@Test
	fun `카테고리 수정시 본인 카테고리가 아니라면 예외`() {
		val givenUser = UserFactory.testLoginUser()
		val otherUser = UserFactory.testUser()
		val category = ScheduleCategoryFactory.category(givenUser)
		val updateCategory = ScheduleCategoryFactory.categoryUpdateInput()
		every { scheduleCategoryRepository.findByIdOrNull(any()) } returns category

		assertThrows<UnauthorizedAccessException> {
			scheduleCategoryService.updateCategory(category.id!!, updateCategory, otherUser)
		}
	}
}
