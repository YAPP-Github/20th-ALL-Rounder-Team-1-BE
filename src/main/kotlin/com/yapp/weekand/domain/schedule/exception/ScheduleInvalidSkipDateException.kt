package com.yapp.weekand.domain.schedule.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class ScheduleInvalidSkipDateException : AbstractBaseGraphQLException(ErrorCode.SCHEDULE_INVALID_SKIP_DATE)
