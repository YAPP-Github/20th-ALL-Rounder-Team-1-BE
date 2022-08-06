package com.yapp.weekand.domain.category.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class ScheduleCategoryUnderMinSizeException: AbstractBaseGraphQLException(ErrorCode.SCHEDULE_UNDER_MIN_SIZE)
