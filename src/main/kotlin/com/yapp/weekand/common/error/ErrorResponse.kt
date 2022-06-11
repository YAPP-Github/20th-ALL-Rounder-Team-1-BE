package com.yapp.weekand.common.error

class ErrorResponse(
	val status: Int,
	val code: Int,
	val message: String
) {
	companion object {
		fun error(e: ErrorCode): ErrorResponse {
			return ErrorResponse(e.status.value(), e.code, e.message)
		}
	}
}
