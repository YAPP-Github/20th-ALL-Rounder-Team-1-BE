package com.yapp.weekand.domain.auth.service

import com.yapp.weekand.common.entity.EntityFactory
import com.yapp.weekand.domain.auth.dto.LoginRequest
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.auth.exception.LoginFailException
import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.interest.repository.UserInterestRepository
import com.yapp.weekand.domain.job.repository.UserJobRepository
import com.yapp.weekand.domain.user.repository.UserRepository
import com.yapp.weekand.infra.email.EmailService
import com.yapp.weekand.infra.redis.RedisService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockKExtension::class)
class AuthServiceTest {

	@InjectMockKs
	lateinit var authService: AuthService

	@MockK(relaxed = true)
	lateinit var userRepository: UserRepository

	@MockK(relaxed = true)
	lateinit var userJobRepository: UserJobRepository

	@MockK(relaxed = true)
	lateinit var userInterestRepository: UserInterestRepository

	@MockK
	lateinit var jwtProvider: JwtProvider

	@MockK(relaxed = true)
	lateinit var redisService: RedisService

	@MockK(relaxed = true)
	lateinit var emailService: EmailService

	@MockK
	private var user: User =  EntityFactory.testLoginUser()

	@MockK(relaxed = true)
	private lateinit var passwordEncoder: PasswordEncoder

	@Test
	@DisplayName("로그인을 한다.")
	fun `로그인`() {
		val loginRequest = LoginRequest(email = "test@naver.com", password = "weekand1234!")
		every{ userRepository.findByEmail(anyString())} returns user
		every{ passwordEncoder.matches(any(), any()) } returns true
		every{ user.password } returns passwordEncoder.encode("weekand1234!")
		every{ jwtProvider.createAccessToken(any()) } returns "accesstoken"
		every{ jwtProvider.createRefreshToken() } returns "refreshtoken"

		val resultLogin = authService.login(loginRequest)

		Assertions.assertThat(resultLogin.accessToken).isNotEmpty
	}

	@Test
	@DisplayName("이메일에 일치하는 유저가 없다면 예외를 반환한다.")
	fun `로그인 시 유저가 존재하지 않는 예외`() {
		every{ userRepository.findByEmail(anyString())} returns null

		assertThrows<LoginFailException> {
			authService.login(LoginRequest(email = "test@naver.com", password = "weekand1234!"))
		}
	}

	@Test
	@DisplayName("비밀번호가 일치하지 않다면 예외를 반환한다.")
	fun `로그인 시 비밀번호가 일치하지 않는 예외`() {
		every{ userRepository.findByEmail(anyString())} returns user

		every{ passwordEncoder.matches(any(), any()) } returns false

		assertThrows<LoginFailException> {
			authService.login(LoginRequest(email = "test@naver.com", password = "weekand1234!"))
		}
	}
}
