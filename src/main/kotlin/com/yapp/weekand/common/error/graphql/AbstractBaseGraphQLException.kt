package com.yapp.weekand.common.error.graphql

import com.yapp.weekand.common.error.ErrorCode

interface WeekandError {
	val code: String
}

abstract class AbstractBaseGraphQLException(
	errorCode: ErrorCode,
	cause: Throwable? = null
): RuntimeException(errorCode.message, cause), WeekandError {
	override val code: String = errorCode.code
}
