package com.yapp.weekand.common.error

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ErrorCode(
	val status: HttpStatus,
	val code: Int,
	val message: String
) {
	// Common
	REQUEST_ERROR(HttpStatus.BAD_REQUEST,2000, "입력 값을 확인해주세요."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND,2001, "존재하지 않는 유저입니다."),
	EXPIRED_JWT(HttpStatus.FORBIDDEN,2002, "유효기간이 만료된 JWT입니다."),
	INVALID_JWT(HttpStatus.FORBIDDEN,2003, "유효하지 않은 JWT입니다."),
	INVALID_REFRESH_JWT(HttpStatus.FORBIDDEN,2004, "유효하지 않은 리프레시 토큰입니다."),
	EMPTY_JWT(HttpStatus.FORBIDDEN,2005, "JWT를 입력해주세요."),
	INVALID_USER_JWT(HttpStatus.UNAUTHORIZED,2006,"권한이 없는 유저의 접근입니다."),
	LOGIN_FAIL(HttpStatus.UNAUTHORIZED,2007, "이메일, 비밀번호가 일치하지 않습니다."),
}
