package com.yapp.weekand.common.jwt.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class InvalidRefreshTokenException: AbstractBaseGraphQLException(ErrorCode.INVALID_REFRESH_JWT)
