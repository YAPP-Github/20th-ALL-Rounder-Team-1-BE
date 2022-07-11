package com.yapp.weekand.common.jwt.aop

import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.common.jwt.exception.ExpiredAccessTokenException
import com.yapp.weekand.common.jwt.exception.JwtException
import io.jsonwebtoken.ExpiredJwtException
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class JwtAspect(
	private val jwtProvider: JwtProvider
) {
	@Before("@annotation(jwtAuth)")
	fun authenticateWithToken(jwtAuth: JwtAuth) {
		val requestAttributes = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
		val request = requestAttributes.request
		try {
			val accessToken: String = jwtProvider.resolveAccessToken(request)
			jwtProvider.parseToken(accessToken)
			setAuthentication(accessToken)
		} catch (e: ExpiredJwtException) {
			throw ExpiredAccessTokenException()
		} catch (e: Exception) {
			throw JwtException()
		}
	}

	private fun setAuthentication(accessToken: String) {
		val authentication = jwtProvider.getAuthentication(accessToken)
		SecurityContextHolder.getContext().authentication = authentication
	}
}
