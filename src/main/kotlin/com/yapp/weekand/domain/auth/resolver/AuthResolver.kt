package com.yapp.weekand.domain.auth.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.auth.dto.LoginRequest
import com.yapp.weekand.domain.auth.dto.LoginResponse
import com.yapp.weekand.domain.auth.service.AuthService

@DgsComponent
class AuthResolver(
	private val authService: AuthService
) {
	@DgsQuery
	fun login(@InputArgument loginInput: LoginRequest): LoginResponse {
		return authService.login(loginInput)
	}
}
