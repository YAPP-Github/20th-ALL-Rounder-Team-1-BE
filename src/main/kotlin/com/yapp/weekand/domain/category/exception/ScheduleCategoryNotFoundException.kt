package com.yapp.weekand.domain.category.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class ScheduleCategoryNotFoundException: AbstractBaseGraphQLException(ErrorCode.SCHEDULE_CATEGORY_NOT_FOUND)
