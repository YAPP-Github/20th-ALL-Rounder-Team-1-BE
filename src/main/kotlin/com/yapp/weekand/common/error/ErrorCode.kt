package com.yapp.weekand.common.error

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ErrorCode(
	val status: HttpStatus,
	val code: String,
	val message: String
) {
	// Common
	REQUEST_ERROR(HttpStatus.BAD_REQUEST,"2000", "입력 값을 확인해주세요."),
	WEEKAND_ERROR(HttpStatus.BAD_REQUEST, "2001", "현재 weekand 이용이 불가능 합니다"),

	//Auth
	USER_NOT_FOUND(HttpStatus.NOT_FOUND,"3001", "존재하지 않는 유저입니다."),
	EXPIRED_JWT(HttpStatus.FORBIDDEN,"3002", "유효기간이 만료된 JWT입니다."),
	INVALID_JWT(HttpStatus.FORBIDDEN,"3003", "유효하지 않은 JWT입니다."),
	INVALID_REFRESH_JWT(HttpStatus.FORBIDDEN,"3004", "유효하지 않은 리프레시 토큰입니다."),
	EMPTY_JWT(HttpStatus.FORBIDDEN,"3005", "JWT를 입력해주세요."),
	INVALID_USER_JWT(HttpStatus.UNAUTHORIZED,"3006","권한이 없는 유저의 접근입니다."),
	LOGIN_FAIL(HttpStatus.UNAUTHORIZED,"3007", "이메일, 비밀번호가 일치하지 않습니다."),
	EMAIL_SEND_FAIL(HttpStatus.BAD_REQUEST, "3008", "이메일 전송에 실패하였습니다."),
	EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "3009", "이미 등록된 이메일입니다."),

	//Schedule
	SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND,"4001", "해당 스케줄을 찾을 수 없습니다"),
	SCHEDULE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND,"4002", "해당 카테고리를 찾을 수 없습니다"),

	//Follow
	FOLLOW_DUPLICATED(HttpStatus.BAD_REQUEST, "5001", "이미 등록된 팔로우 내역입니다."),
}
