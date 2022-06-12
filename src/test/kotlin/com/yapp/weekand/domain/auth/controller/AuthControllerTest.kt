package com.yapp.weekand.domain.auth.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.yapp.weekand.common.entity.EntityFactory
import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.auth.dto.ReissueAccessTokenResponse
import com.yapp.weekand.domain.auth.service.AuthService
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@WebMvcTest(AuthController::class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockKExtension::class)
class AuthControllerTest {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@MockkBean
	private lateinit var authService: AuthService

	@MockkBean
	lateinit var jwtProvider: JwtProvider

	@Test
	@DisplayName("로그인을 한다")
	fun `로그인`() {
	    //given
		val loginRequest = EntityFactory.loginRequest()
		val loginResponse = EntityFactory.loginResponse()

		//when
		every { authService.login(any()) } returns loginResponse

		//then
		mockMvc
			.perform(MockMvcRequestBuilders.post("/api/v1/login")
				.content(jacksonObjectMapper().writeValueAsString(loginRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk)
			.andExpect(jsonPath("$.accessToken").value(loginResponse.accessToken))
			.andExpect(jsonPath("$.refreshToken").value(loginResponse.refreshToken))
	}

	@Test
	@DisplayName("access token을 재발급 받는다.")
	fun `access token 재발급`() {
		//given
		val reissueAccessTokenResponse = ReissueAccessTokenResponse("NewAccessToken")

		//when
		every { jwtProvider.resolveRefreshToken(any()) } returns "refreshToken"
		every { jwtProvider.resolveAccessToken(any()) } returns "preAccessToken"
		every { authService.renewAccessToken(any(), any()) } returns reissueAccessTokenResponse

		//then
		mockMvc
			.perform(MockMvcRequestBuilders.post("/api/v1/refresh")
				.header("Access-Token", "preAccessToken")
				.header("Refresh-Token", "refreshtoken"))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk)
			.andExpect(jsonPath("$.accessToken").value(reissueAccessTokenResponse.accessToken))
	}
}
