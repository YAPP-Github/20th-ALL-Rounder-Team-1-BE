package com.yapp.weekand.common.jwt

import com.yapp.weekand.common.jwt.user.UserDetailServiceImpl
import com.yapp.weekand.common.jwt.user.UserDetailsImpl
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.Authentication
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
@Transactional(readOnly = true)
class JwtProvider(
	private val userDetailsService: UserDetailServiceImpl,
	@Value("\${jwt.secret}") private val secretKey: String,
	@Value("\${jwt.access-token-expiry}") private val accessTokenValidTime: Int,
	@Value("\${jwt.refresh-token-expiry}") private val refreshTokenValidTime: Int,
	@Value("\${jwt.access-token-header}") private val ACCESS_TOKEN_HEADER: String,
	@Value("\${jwt.refresh-token-header}") private val REFRESH_TOKEN_HEADER: String
)
{
	private val key:Key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))

	fun createAccessToken(email: String): String{
		val claims: Claims = Jwts.claims().setSubject(email)
		val now = Date()
		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(Date(now.time + accessTokenValidTime))
			.signWith(SignatureAlgorithm.HS256, key)
			.compact()
	}

	fun createRefreshToken(): String{
		val now = Date()
		return Jwts.builder()
			.setIssuedAt(now)
			.setExpiration(Date(now.time + refreshTokenValidTime))
			.signWith(SignatureAlgorithm.HS256, key)
			.compact()
	}

	fun getAuthentication(token: String): Authentication {
		val userDetails = userDetailsService.loadUserByUsername(getUserPk(token))
		return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
	}

	fun getFromSecurityContextHolder(): UserDetailsImpl {
		return SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
	}

	fun getUserPk(token: String): String {
		return try {
			parseToken(token).body.subject
		} catch (e: ExpiredJwtException) {
			e.claims.subject
		}
	}

	fun resolveAccessToken(request: HttpServletRequest): String {
		return request.getHeader(ACCESS_TOKEN_HEADER)
	}

	fun resolveRefreshToken(request: HttpServletRequest): String {
		return request.getHeader(REFRESH_TOKEN_HEADER)
	}

	fun parseToken(token: String): Jws<Claims> {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
	}
}
