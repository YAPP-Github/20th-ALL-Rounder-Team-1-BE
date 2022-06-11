package com.yapp.weekand.common.error.exception

import com.yapp.weekand.common.error.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@RestControllerAdvice
class ExceptionHandler {

	@ExceptionHandler(BaseException::class)
	fun handleBaseException(e: BaseException): ResponseEntity<ErrorResponse> {
		return ResponseEntity.status(e.errorCode.status).body(ErrorResponse.error(e.errorCode))
	}
}
