package com.yapp.weekand.domain.auth.resolver

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.ninjasquad.springmockk.MockkBean
import com.yapp.weekand.common.entity.EntityFactory
import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.auth.service.AuthService
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(classes = [DgsAutoConfiguration::class, AuthResolver::class])
class AuthResolverTest{

	@Autowired
	lateinit var dgsQueryExecutor: DgsQueryExecutor

	@MockkBean
	private lateinit var authService: AuthService

	@MockkBean
	lateinit var jwtProvider: JwtProvider

	@Test
	@DisplayName("로그인 성공시 Access, Refresh Token 발급")
	fun `로그인`() {
		//given
		val loginResponse = EntityFactory.loginResponse()

		//when
		every { authService.login(any()) } returns loginResponse

		val response: List<String> =dgsQueryExecutor.executeAndExtractJsonPath("""
                    query{
						login(loginInput: {
							email: "email@test.com",
							password: "password"
						}){
							refreshToken,
							accessToken
						}
					}
                """.trimIndent(), "data.login.*")

		//then
		assertThat(response).contains(loginResponse.accessToken)
		assertThat(response).contains(loginResponse.refreshToken)
	}
}
