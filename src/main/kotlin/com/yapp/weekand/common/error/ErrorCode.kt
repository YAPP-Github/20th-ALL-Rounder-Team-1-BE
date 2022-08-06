package com.yapp.weekand.common.error

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ErrorCode(
	val status: HttpStatus,
	val code: String,
	val message: String
) {
	//Common
	REQUEST_ERROR(HttpStatus.BAD_REQUEST,"2000", "입력 값을 확인해주세요."),
	WEEKAND_ERROR(HttpStatus.BAD_REQUEST, "2001", "현재 weekand 이용이 불가능 합니다"),
	FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "2002", "파일 업로드에 실패하였습니다."),

	//Auth
	USER_NOT_FOUND(HttpStatus.NOT_FOUND,"3001", "존재하지 않는 유저입니다."),
	EXPIRED_JWT(HttpStatus.FORBIDDEN,"3002", "유효기간이 만료된 JWT입니다."),
	INVALID_JWT(HttpStatus.FORBIDDEN,"3003", "접근이 거부되었습니다."),
	INVALID_REFRESH_JWT(HttpStatus.FORBIDDEN,"3004", "유효하지 않은 리프레시 토큰입니다."),
	EMPTY_JWT(HttpStatus.FORBIDDEN,"3005", "JWT를 입력해주세요."),
	INVALID_USER(HttpStatus.UNAUTHORIZED,"3006","권한이 없는 유저의 접근입니다."),
	LOGIN_FAIL(HttpStatus.UNAUTHORIZED,"3007", "이메일, 비밀번호가 일치하지 않습니다."),
	EMAIL_SEND_FAIL(HttpStatus.BAD_REQUEST, "3008", "이메일 전송에 실패하였습니다."),
	EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "3009", "이미 등록된 이메일입니다."),
	SIGNUP_FAIL(HttpStatus.BAD_REQUEST, "3010", "회원가입에 실패하였습니다."),
	INVALID_FORMATTED_PASSWORD(HttpStatus.BAD_REQUEST, "3011", "올바른 비밀번호 형식이 아닙니다."),
	INVALID_FORMATTED_EMAIL(HttpStatus.BAD_REQUEST, "3012", "올바른 이메일 형식이 아닙니다."),
	INVALID_FORMATTED_NICKNAME(HttpStatus.BAD_REQUEST, "3013", "올바른 닉네임 형식이 아닙니다."),
	NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "3014", "이미 등록된 닉네임입니다."),
	PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "3015", "비밀번호가 일치하지 않습니다."),
	GOAL_MAX_LENGTH_EXCEED(HttpStatus.BAD_REQUEST, "3016", "한 줄 목표 최대 길이를 초과하였습니다."),
	NICKNAME_UNDER_MIN_LENGTH(HttpStatus.BAD_REQUEST, "3017", "닉네임이 최소 길이 미만입니다."),
	NICKNAME_MAX_LENGTH_EXCEED(HttpStatus.BAD_REQUEST, "3018", "닉네임 최대 길이를 초과하였습니다."),
	LOGOUT_JWT(HttpStatus.UNAUTHORIZED, "3019", "로그아웃한 회원입니다."),

	//Schedule
	SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND,"4001", "해당 스케줄을 찾을 수 없습니다."),
	SCHEDULE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND,"4002", "해당 카테고리를 찾을 수 없습니다."),
	SCHEDULE_INVALID_DATE(HttpStatus.BAD_REQUEST, "4003", "시작 일시와 종료 일시를 확인해주세요."),
	SCHEDULE_INVALID_CREATE(HttpStatus.BAD_REQUEST, "4004", "반복 요일을 입력해주세요."),
	SCHEDULE_DUPLICATED_STATUS(HttpStatus.BAD_REQUEST, "4005", "이미 상태가 등록된 스케줄입니다."),
	SCHEDULE_INVALID_SKIP_DATE(HttpStatus.BAD_REQUEST, "4006", "스킵 날짜를 확인해주세요."),
	SCHEDULE_CATEGORY_DUPLICATED_NAME(HttpStatus.BAD_REQUEST, "4007", "해당 카테고리명은 이미 사용중입니다."),
	SCHEDULE_UNDER_MIN_SIZE(HttpStatus.BAD_REQUEST, "4008", "최소 2개 이상의 카테고리가 존재할 시 삭제 가능합니다."),
	SCHEDULE_INVALID_STATE_DATE(HttpStatus.BAD_REQUEST, "4009", "상태 추가할 날짜를 확인해주세요."),

	//Follow
	FOLLOW_DUPLICATED(HttpStatus.BAD_REQUEST, "5001", "이미 등록된 팔로우 내역입니다."),
	FOLLOW_NOT_FOUND(HttpStatus.BAD_REQUEST, "5002", "팔로우 관계가 아닙니다."),

	//Email
	EMAIL_REPLACEMENT_INVALID(HttpStatus.BAD_REQUEST, "6001", "이메일 내용 형식이 올바르지 않습니다."),
}
