package com.yapp.weekand.domain.category.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class ScheduleCategoryDuplicatedNameException: AbstractBaseGraphQLException(ErrorCode.SCHEDULE_CATEGORY_DUPLICATED_NAME)
