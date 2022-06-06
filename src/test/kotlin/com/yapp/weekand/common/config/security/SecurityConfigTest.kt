package com.yapp.weekand.common.config.security

import com.yapp.weekand.common.entity.EntityFactory
import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.user.repository.UserRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class SecurityConfigTest{

	@Autowired
	lateinit var userRepository: UserRepository

	@Autowired
	lateinit var jwtProvider: JwtProvider

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Test
	@DisplayName("access token 으로 필터를 통과한다.")
	fun `access token 필터 통과`() {

		userRepository.save(
			EntityFactory.testLoginUser()
		)

		val accessToken = jwtProvider.createAccessToken("test@naver.com")

		mockMvc
			.perform(post("/pass-filter").header("Access-Token", accessToken))
			.andExpect(status().isNotFound)
			.andDo(print())
	}

	@Test
	@DisplayName("access token이 empty일 경우 예외를 반환한다.")
	fun `access token empty 에러`() {
		mockMvc
			.perform(post("/empty-token-error"))
			.andExpect(jsonPath("$.code").value(ErrorCode.EMPTY_JWT.code))
			.andExpect(jsonPath("$.message").value(ErrorCode.EMPTY_JWT.message))
			.andDo(print())
	}

	@Test
	@DisplayName("access token이 유효하지 않다면 예외를 반환한다.")
	fun `access token 유효기간 에러`() {
		mockMvc
			.perform(post("/invalid-token-error").header("Access-Token", "accesstoken"))
			.andExpect(jsonPath("$.code").value(ErrorCode.INVALID_JWT.code))
			.andExpect(jsonPath("$.message").value(ErrorCode.INVALID_JWT.message))
			.andDo(print())
	}
}
