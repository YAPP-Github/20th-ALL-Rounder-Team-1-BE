package com.yapp.weekand.domain.category.repository

import com.yapp.weekand.common.entity.UserFactory
import com.yapp.weekand.domain.category.entity.ScheduleCategory
import com.yapp.weekand.domain.category.entity.ScheduleCategoryOpenType
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.user.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class ScheduleCategoryRepositoryTest {
	@Autowired
	lateinit var scheduleCategoryRepository: ScheduleCategoryRepository

	@Autowired
	lateinit var userRepository: UserRepository

	lateinit var givenTestUser: User
	lateinit var dummyScheduleCategories: List<ScheduleCategory>

	@BeforeEach
	fun beforeEach() {
		givenTestUser = userRepository.save(UserFactory.testLoginUser())
		dummyScheduleCategories = listOf<ScheduleCategory>(
			ScheduleCategory(
				id = 1,
				user = givenTestUser,
				name = "카테고리 1",
				color = "red",
				openType = ScheduleCategoryOpenType.ALL_OPEN,
			),
			ScheduleCategory(
				id = 2,
				user = givenTestUser,
				name = "카테고리 2",
				color = "blue",
				openType = ScheduleCategoryOpenType.FOLLOWER_OPEN
			),
			ScheduleCategory(
				id = 3,
				user = givenTestUser,
				name = "카테고리 3",
				color = "yellow",
				openType = ScheduleCategoryOpenType.CLOSED
			),
		)
		scheduleCategoryRepository.saveAll(dummyScheduleCategories)
	}

	@Test
	fun `일정 카테고리 목록을 주어진 조건에 맞게 반환한다 - name desc`() {
		val givenPageable = PageRequest.of(0,5, Sort.by("name").descending())

		val result = scheduleCategoryRepository.findByUser(givenTestUser, givenPageable)

		Assertions.assertEquals(listOf(
			dummyScheduleCategories[2].name,
			dummyScheduleCategories[1].name,
			dummyScheduleCategories[0].name,
		), result.content.map { it.name })
	}

	@Test
	fun `일정 카테고리 목록을 주어진 조건에 맞게 반환한다 - name asc`() {
		val givenPageable = PageRequest.of(0,5, Sort.by("name").ascending())

		val result = scheduleCategoryRepository.findByUser(givenTestUser, givenPageable)

		Assertions.assertEquals(listOf(
			dummyScheduleCategories[0].name,
			dummyScheduleCategories[1].name,
			dummyScheduleCategories[2].name,
		), result.content.map { it.name })
	}

	@Test
	fun `일정 카테고리 목록을 주어진 조건에 맞게 반환한다 - dateCreated desc`() {
		val givenPageable = PageRequest.of(0,5, Sort.by("dateCreated").descending())

		val result = scheduleCategoryRepository.findByUser(givenTestUser, givenPageable)

		Assertions.assertEquals(listOf(
			dummyScheduleCategories[2].dateCreated,
			dummyScheduleCategories[1].dateCreated,
			dummyScheduleCategories[0].dateCreated,
		), result.content.map { it.dateCreated })
	}

	@Test
	fun `일정 카테고리 목록을 주어진 조건에 맞게 반환한다 - dateCreated asc`() {
		val givenPageable = PageRequest.of(0,5, Sort.by("dateCreated").ascending())

		val result = scheduleCategoryRepository.findByUser(givenTestUser, givenPageable)

		Assertions.assertEquals(listOf(
			dummyScheduleCategories[0].dateCreated,
			dummyScheduleCategories[1].dateCreated,
			dummyScheduleCategories[2].dateCreated,
		), result.content.map { it.dateCreated })
	}
}
