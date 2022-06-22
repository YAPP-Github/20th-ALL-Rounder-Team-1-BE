package com.yapp.weekand.domain.schedule.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class ScheduleNotFoundException: AbstractBaseGraphQLException(ErrorCode.SCHEDULE_NOT_FOUND)
