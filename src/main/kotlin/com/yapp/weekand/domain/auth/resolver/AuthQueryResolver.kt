package com.yapp.weekand.domain.auth.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData
import com.yapp.weekand.api.generated.types.ValidAuthKeyInput
import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.auth.dto.LoginRequest
import com.yapp.weekand.domain.auth.dto.LoginResponse
import com.yapp.weekand.domain.auth.dto.ReissueAccessTokenResponse
import com.yapp.weekand.domain.auth.service.AuthService
import org.springframework.web.context.request.ServletWebRequest

@DgsComponent
class AuthQueryResolver(
	private val authService: AuthService,
	private val jwtProvider: JwtProvider
) {
	@DgsQuery
	fun login(@InputArgument loginInput: LoginRequest): LoginResponse {
		return authService.login(loginInput)
	}

	@DgsQuery
	fun reissue(dfe: DgsDataFetchingEnvironment): ReissueAccessTokenResponse {
		val requestData: DgsWebMvcRequestData = dfe.getDgsContext().requestData as (DgsWebMvcRequestData)
		val webRequest = requestData.webRequest as (ServletWebRequest)
		val refreshToken:String = jwtProvider.resolveRefreshToken(webRequest.request)
		return authService.reissueAccessToken(refreshToken)
	}

	@DgsQuery
	fun sendAuthKey(email: String): String {
		authService.sendEmailAuthKey(email)
		return "succeed"
	}

	@DgsQuery
	fun validAuthKey(validAuthKeyInput: ValidAuthKeyInput) = authService.isValidAuthKey(validAuthKeyInput)
}
