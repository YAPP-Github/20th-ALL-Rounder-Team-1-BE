package com.yapp.weekand.domain.auth.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class InvalidPasswordException: AbstractBaseGraphQLException(ErrorCode.INVALID_FORMATTED_PASSWORD)
