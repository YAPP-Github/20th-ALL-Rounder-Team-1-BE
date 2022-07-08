package com.yapp.weekand.domain.user.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class GoalMaxLengthExceedException: AbstractBaseGraphQLException(ErrorCode.GOAL_MAX_LENGTH_EXCEED)
