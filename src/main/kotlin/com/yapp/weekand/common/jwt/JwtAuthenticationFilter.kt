package com.yapp.weekand.common.jwt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.ErrorResponse
import com.yapp.weekand.common.util.Logger
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(
	private val jwtProvider: JwtProvider
): GenericFilterBean() {
	override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
		try {
			val accessToken: String = jwtProvider.resolveAccessToken(request as HttpServletRequest)
			jwtProvider.parseToken(accessToken)
			setAuthentication(accessToken)
		}  catch (e: ExpiredJwtException) {
			Logger.info(e.message)
			sendErrorResponse(response as HttpServletResponse, ErrorCode.EXPIRED_JWT)
			return
		} catch (e: NullPointerException) {
			Logger.info(e.message)
		} catch (e: Exception) {
			Logger.info(e.message)
		}
		chain.doFilter(request, response)
	}

	private fun setAuthentication(accessToken: String) {
		val authentication = jwtProvider.getAuthentication(accessToken)
		SecurityContextHolder.getContext().authentication = authentication
	}

	private fun sendErrorResponse(response: HttpServletResponse, errorCode: ErrorCode) {
		val errorResponse = ErrorResponse.error(errorCode)
		val mapper = jacksonObjectMapper()
		response.characterEncoding = "UTF-8"
		response.status = errorCode.status.value()
		response.contentType = MediaType.APPLICATION_JSON_VALUE
		response.writer.write(mapper.writeValueAsString(errorResponse))
	}
}
