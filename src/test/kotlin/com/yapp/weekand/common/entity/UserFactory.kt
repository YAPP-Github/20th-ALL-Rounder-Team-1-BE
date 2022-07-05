package com.yapp.weekand.common.entity

import com.yapp.weekand.domain.auth.dto.LoginRequest
import com.yapp.weekand.domain.auth.dto.LoginResponse
import com.yapp.weekand.domain.user.entity.User

class UserFactory {
	companion object{
		fun testLoginUser() = User(
			email= "test@naver.com",
			nickname = "위캔드",
			password = "weekand1234!"
		)

		fun loginRequest() = LoginRequest(
			email = "test",
			password = "weekand1234!"
		)

		fun loginResponse() = LoginResponse(
			accessToken = "accessToken",
			refreshToken = "refreshToken"
		)

		fun testUser() = User (
			email= "test1234@gmail.com",
			nickname = "위캔두두",
			password = "weekand1234!"
		)
	}
}
