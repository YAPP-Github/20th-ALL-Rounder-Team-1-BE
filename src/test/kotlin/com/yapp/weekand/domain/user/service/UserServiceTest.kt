package com.yapp.weekand.domain.user.service

import com.yapp.weekand.common.entity.UserFactory.Companion.testLoginUser
import com.yapp.weekand.common.entity.UserFactory.Companion.userInterest
import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.interest.repository.UserInterestRepository
import com.yapp.weekand.domain.job.repository.UserJobRepository
import com.yapp.weekand.domain.user.repository.UserRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UserServiceTest {
	@InjectMockKs
	lateinit var userService: UserService

	@MockK(relaxed = true)
	lateinit var userRepository: UserRepository

	@MockK(relaxed = true)
	lateinit var userJobRepository: UserJobRepository

	@MockK(relaxed = true)
	lateinit var userInterestRepository: UserInterestRepository

	@MockK
	lateinit var jwtProvider: JwtProvider

	@Test
	fun `유저의 관심사를 수정한다`() {
		val interests = listOf("수정 관심사1")
		val user = testLoginUser()
		every { userInterestRepository.existsByUser(any()) } returns true
		every { userInterestRepository.findByUser(any()) } returns userInterest(user)
		every { userInterestRepository.deleteAll(any()) } just Runs

		userService.updateInterests(user, interests)

		Assertions.assertThat(user.interests).size().isEqualTo(1)
		assertEquals(user.interests[0].interestName, interests[0])
	}
}
