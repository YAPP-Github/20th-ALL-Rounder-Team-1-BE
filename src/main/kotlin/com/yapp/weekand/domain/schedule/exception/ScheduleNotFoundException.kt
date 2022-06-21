package com.yapp.weekand.domain.schedule.exception

import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class ScheduleNotFoundException(
	message: String,
	cause: Throwable? = null
): AbstractBaseGraphQLException(message, cause) {
	override val code: String = "schedule not found"
}
