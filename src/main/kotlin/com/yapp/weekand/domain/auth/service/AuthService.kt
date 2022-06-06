package com.yapp.weekand.domain.auth.service

import com.yapp.weekand.domain.auth.exception.UserNotFoundException
import com.yapp.weekand.common.util.Logger
import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.auth.dto.LoginRequest
import com.yapp.weekand.domain.auth.dto.LoginResponse
import com.yapp.weekand.domain.auth.dto.ReissueAccessTokenResponse
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.auth.exception.InvalidTokenException
import com.yapp.weekand.domain.auth.exception.LoginFailException
import com.yapp.weekand.domain.user.repository.UserRepository
import com.yapp.weekand.infra.redis.RedisService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService (
	private val userRepository: UserRepository,
	private val jwtProvider: JwtProvider,
	private val redisService: RedisService,
	private val passwordEncoder: PasswordEncoder
) {

	@Value("\${jwt.refresh-token-expiry}")
	private val refreshTokenExpiry: Int = 0

	fun login(loginRequest: LoginRequest): LoginResponse {
		val user = userRepository.findByEmail(loginRequest.email)
			?: throw LoginFailException()

		if (!passwordEncoder.matches(loginRequest.password, user.password)) {
			throw LoginFailException()
		}

		val accessToken = jwtProvider.createAccessToken(user.email)
		val refreshToken = jwtProvider.createRefreshToken()
		redisService.setValue(refreshToken, user.email, refreshTokenExpiry)
		Logger.info(refreshTokenExpiry.toString())

		return LoginResponse(
			accessToken = accessToken,
			refreshToken = refreshToken
		)
	}

	fun renewAccessToken(accessToken: String, refreshToken: String): ReissueAccessTokenResponse {
		val user: User = userRepository.findByEmail(jwtProvider.getUserPk(accessToken))
			?: throw UserNotFoundException()

		val email: String = redisService.getValue(refreshToken)
			?: throw InvalidTokenException()

		if (email != user.email) {
			throw InvalidTokenException()
		}

		return ReissueAccessTokenResponse(jwtProvider.createAccessToken(user.email))
	}
}
