package com.yapp.weekand.common.entity

import com.yapp.weekand.domain.auth.dto.LoginRequest
import com.yapp.weekand.domain.auth.dto.LoginResponse
import com.yapp.weekand.domain.interest.entity.UserInterest
import com.yapp.weekand.domain.user.entity.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

class UserFactory {
	companion object{
		val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

		fun testLoginUser() = User(
			email= "test@naver.com",
			nickname = "위캔드",
			password = passwordEncoder.encode("weekand1234!"),
			marketingAgreed = true)

		fun loginRequest() = LoginRequest(
			email = "test",
			password = "weekand1234!"
		)

		fun loginResponse() = LoginResponse(
			accessToken = "accessToken",
			refreshToken = "refreshToken"
		)

		fun userInterest(user: User) = listOf(
			UserInterest(
				interestName = "관심사1",
				user = user
			),
			UserInterest(
				interestName = "관심사2",
				user = user
			)
		)
	}
}
