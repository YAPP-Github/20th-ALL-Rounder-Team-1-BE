package com.yapp.weekand.domain.schedule.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class ScheduleDuplicatedStatusException : AbstractBaseGraphQLException(ErrorCode.SCHEDULE_DUPLICATED_STATUS)
