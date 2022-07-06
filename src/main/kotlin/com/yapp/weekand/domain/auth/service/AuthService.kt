package com.yapp.weekand.domain.auth.service

import com.yapp.weekand.api.generated.types.PasswordInput
import com.yapp.weekand.api.generated.types.SignUpInput
import com.yapp.weekand.api.generated.types.ValidAuthKeyInput
import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.common.util.Constants.Companion.AUTH_KEY_PREFIX
import com.yapp.weekand.common.util.Constants.Companion.REFRESH_TOKEN_PREFIX
import com.yapp.weekand.common.util.Constants.Companion.TEMP_PASSWORD_PREFIX
import com.yapp.weekand.domain.auth.dto.LoginRequest
import com.yapp.weekand.domain.auth.dto.LoginResponse
import com.yapp.weekand.domain.auth.dto.ReissueAccessTokenResponse
import com.yapp.weekand.domain.auth.exception.*
import com.yapp.weekand.domain.interest.entity.UserInterest
import com.yapp.weekand.domain.interest.repository.UserInterestRepository
import com.yapp.weekand.domain.job.entity.UserJob
import com.yapp.weekand.domain.job.repository.UserJobRepository
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.user.repository.UserRepository
import com.yapp.weekand.infra.email.EmailService
import com.yapp.weekand.infra.email.replacement.AuthEmailReplacement
import com.yapp.weekand.infra.email.replacement.TempPasswordEmailReplacement
import com.yapp.weekand.infra.redis.RedisService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
@Transactional(readOnly = true)
class AuthService(
	private val userRepository: UserRepository,
	private val jwtProvider: JwtProvider,
	private val redisService: RedisService,
	private val passwordEncoder: PasswordEncoder,
	private val emailService: EmailService,
	private val userInterestRepository: UserInterestRepository,
	private val userJobRepository: UserJobRepository
) {
	@Value("\${jwt.refresh-token-expiry}")
	private val refreshTokenExpiry: Long = 0

	@Value("\${mail.authkey-expiry}")
	private val authKeyExpiry: Long = 0

	private val tempPasswordCharPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
	private val tempPasswordLength = 8;

	fun login(loginRequest: LoginRequest): LoginResponse {
		val user = userRepository.findByEmail(loginRequest.email)
			?: throw LoginFailException()

		if (!passwordEncoder.matches(loginRequest.password, user.password)) {
			throw LoginFailException()
		}

		val accessToken = jwtProvider.createAccessToken(user.email)
		val refreshToken = jwtProvider.createRefreshToken()
		redisService.setValue("$REFRESH_TOKEN_PREFIX:$refreshToken", user.email, refreshTokenExpiry)

		return LoginResponse(
			accessToken = accessToken,
			refreshToken = refreshToken
		)
	}

	fun reissueAccessToken(refreshToken: String): ReissueAccessTokenResponse {
		val email: String = redisService.getValue("$REFRESH_TOKEN_PREFIX:$refreshToken")
			?: throw InvalidTokenException()

		userRepository.findByEmail(email) ?: throw UserNotFoundException()
		return ReissueAccessTokenResponse(jwtProvider.createAccessToken(email))
	}

	fun sendEmailAuthKey(email: String) {
		if (userRepository.existsUserByEmail(email)) {
			throw EmailDuplicatedException()
		}
		val authKey = createAuthKey()
		redisService.setValue("$AUTH_KEY_PREFIX:$email", authKey, authKeyExpiry)

		val replacement = AuthEmailReplacement(
			mapOf("code" to authKey)
		)
		emailService.sendEmail(email, replacement)
	}

	fun sendTempPassword(email: String) {
		userRepository.findByEmail(email) ?: throw UserNotFoundException()

		val tempPassword = createTempPassword()
		redisService.setValueNoExpire("$TEMP_PASSWORD_PREFIX:$email", tempPassword)

		val replacements = TempPasswordEmailReplacement(
			mapOf("userEmail" to email, "tempPassword" to tempPassword)
		)
		emailService.sendEmail(email, replacements)
	}

	fun isValidAuthKey(request: ValidAuthKeyInput): Boolean {
		val authKey = redisService.getValue("$AUTH_KEY_PREFIX:" + request.email)
			?: return false
		if (authKey != request.authKey) {
			return false
		}
		return true
	}

	@Transactional
	fun signUp(signUpInput: SignUpInput) {
		if (userRepository.existsUserByEmail(signUpInput.email)) {
			throw EmailDuplicatedException()
		}

		if (userRepository.existsUserByNickname(signUpInput.nickname)) {
			throw NicknameDuplicatedException()
		}

		val user = User(
			nickname = signUpInput.nickname,
			email = signUpInput.email,
			password = passwordEncoder.encode(signUpInput.password)
		)

		userRepository.save(user)

		if (signUpInput.interests != null) {
			saveInterests(signUpInput.interests, user)
		}

		if (signUpInput.jobs != null) {
			saveJobs(signUpInput.jobs, user)
		}
	}

	@Transactional
	fun updatePassword(user: User, passwordInput: PasswordInput) {
		if (!passwordEncoder.matches(passwordInput.oldPassword, user.password)) {
			throw PasswordNotMatchException()
		}
		user.updatePassword(passwordEncoder.encode(passwordInput.newPassword))
	}

	private fun saveJobs(jobs: List<String>, user: User) {
		for (job in jobs) {
			userJobRepository.save(
				UserJob(
					user = user,
					jobName = job
				)
			)
		}
	}

	private fun saveInterests(interests: List<String>, user: User) {
		for (interest in interests) {
			userInterestRepository.save(
				UserInterest(
					user = user,
					interestName = interest
				)
			)
		}
	}

	private fun createAuthKey(): String {
		val random = Random(System.currentTimeMillis())
		var authKey = StringBuilder()
		for (i: Int in 0..7) {
			when (i) {
				0, 1 -> authKey.append((random.nextInt(26) + 65).toChar())
				else -> authKey.append(random.nextInt(10))
			}
		}
		return authKey.toString()
	}

	private fun createTempPassword(): String {
		return (1..tempPasswordLength)
			.map { Random.nextInt(0, tempPasswordCharPool.size) }
			.map(tempPasswordCharPool::get)
			.joinToString("")
	}
}
