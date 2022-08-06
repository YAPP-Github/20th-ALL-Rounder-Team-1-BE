package com.yapp.weekand.graphql

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class WeekandException: AbstractBaseGraphQLException(ErrorCode.WEEKAND_ERROR)
