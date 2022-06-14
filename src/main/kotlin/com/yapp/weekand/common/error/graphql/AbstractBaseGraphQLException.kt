package com.yapp.weekand.common.error.graphql

interface WeekandError {
	val code: String
}

abstract class AbstractBaseGraphQLException(
	message: String,
	cause: Throwable? = null
): RuntimeException(message, cause), WeekandError {
	abstract override val code: String
}
