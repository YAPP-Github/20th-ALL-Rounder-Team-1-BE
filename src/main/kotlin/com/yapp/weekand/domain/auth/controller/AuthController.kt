package com.yapp.weekand.domain.auth.controller

import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.auth.dto.LoginRequest
import com.yapp.weekand.domain.auth.dto.LoginResponse
import com.yapp.weekand.domain.auth.dto.ReissueAccessTokenResponse
import com.yapp.weekand.domain.auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api/v1")
class AuthController(
	private val authService: AuthService,
	private val jwtProvider: JwtProvider
) {

	@PostMapping("/login")
	fun login(@RequestBody loginRequestInput: LoginRequest): ResponseEntity<LoginResponse> {
		return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequestInput))
	}

	@PostMapping("/refresh")
	fun renewAccessToken(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<ReissueAccessTokenResponse> {
		val refreshToken:String = jwtProvider.resolveRefreshToken(request)
		val accessToken:String = jwtProvider.resolveAccessToken(request)
		return ResponseEntity.status(HttpStatus.OK).body(authService.renewAccessToken(accessToken, refreshToken))
	}
}
