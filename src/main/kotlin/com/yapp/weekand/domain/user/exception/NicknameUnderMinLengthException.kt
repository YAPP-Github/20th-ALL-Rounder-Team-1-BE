package com.yapp.weekand.domain.user.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class NicknameUnderMinLengthException: AbstractBaseGraphQLException(ErrorCode.NICKNAME_UNDER_MIN_LENGTH)
