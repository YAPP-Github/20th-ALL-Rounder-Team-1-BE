package com.yapp.weekand.graphql

import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class WeekandException(
	message: String,
	cause: Throwable? = null
) : AbstractBaseGraphQLException(message, cause) {
	override val code: String = "weekand unavailable"
}
