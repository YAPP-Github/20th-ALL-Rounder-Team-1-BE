package com.yapp.weekand.common.jwt.aop

import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.common.jwt.exception.ExpiredAccessTokenException
import com.yapp.weekand.common.jwt.exception.JwtException
import com.yapp.weekand.common.util.Constants
import com.yapp.weekand.domain.auth.exception.JwtInBlackListException
import com.yapp.weekand.infra.redis.RedisService
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
	private val jwtProvider: JwtProvider,
	private val redisService: RedisService
) {
	@Before("@annotation(jwtAuth)")
	fun authenticateWithToken(jwtAuth: JwtAuth) {
		val requestAttributes = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
		val request = requestAttributes.request
		val accessToken: String = jwtProvider.resolveAccessToken(request)

		try {
			jwtProvider.parseToken(accessToken)
		} catch (e: ExpiredJwtException) {
			throw ExpiredAccessTokenException()
		} catch (e: Exception) {
			throw JwtException()
		}

		if (checkLogout(accessToken)) {
			throw JwtInBlackListException()
		}
		setAuthentication(accessToken)
	}

	private fun setAuthentication(accessToken: String) {
		val authentication = jwtProvider.getAuthentication(accessToken)
		SecurityContextHolder.getContext().authentication = authentication
	}

	private fun checkLogout(accessToken: String): Boolean{
		redisService.getValue("${Constants.ACCESS_TOKEN_LOGOUT_PREFIX}:$accessToken")
			?: return false
		return true
	}
}
