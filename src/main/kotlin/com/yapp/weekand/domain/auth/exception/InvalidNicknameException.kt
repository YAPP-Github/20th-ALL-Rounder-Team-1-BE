package com.yapp.weekand.domain.auth.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class InvalidNicknameException: AbstractBaseGraphQLException(ErrorCode.INVALID_FORMATTED_NICKNAME)
