package com.yapp.weekand.domain.auth.service

import com.yapp.weekand.api.generated.types.PasswordInput
import com.yapp.weekand.common.entity.UserFactory
import com.yapp.weekand.domain.auth.dto.LoginRequest
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.auth.exception.LoginFailException
import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.auth.exception.InvalidPasswordException
import com.yapp.weekand.domain.auth.exception.PasswordNotMatchException
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
import org.assertj.core.api.Assertions.assertThat
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
	private var user: User =  UserFactory.testLoginUser()

	@MockK(relaxed = true)
	private lateinit var passwordEncoder: PasswordEncoder

	@Test
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
	fun `로그인 시 유저가 존재하지 않는 예외`() {
		every{ userRepository.findByEmail(anyString())} returns null

		assertThrows<LoginFailException> {
			authService.login(LoginRequest(email = "test@naver.com", password = "weekand1234!"))
		}
	}

	@Test
	fun `로그인 시 비밀번호가 일치하지 않는 예외`() {
		every{ userRepository.findByEmail(anyString())} returns user

		every{ passwordEncoder.matches(any(), any()) } returns false

		assertThrows<LoginFailException> {
			authService.login(LoginRequest(email = "test@naver.com", password = "weekand1234!"))
		}
 	}

	@Test
	fun `비밀번호 수정시 기존 비밀번호가 일치하지 않으면 예외 반환`() {
		val user = UserFactory.testLoginUser()
		val passwordInput = PasswordInput(oldPassword = "1234567a", newPassword = "test12345")
		assertThrows<PasswordNotMatchException> {
			authService.updatePassword(user, passwordInput)
		}
	}

	@Test
	fun `비밀번호 수정`() {
		val user = UserFactory.testLoginUser()
		val passwordInput = PasswordInput(oldPassword = "weekand1234", newPassword = "test12345")

		every{ passwordEncoder.matches(passwordInput.oldPassword, user.password)} returns true
		every { passwordEncoder.encode(passwordInput.newPassword)} returns "test12345"
		authService.updatePassword(user, passwordInput)

		assertThat(user.password).isEqualTo(passwordInput.newPassword)
	}
}
