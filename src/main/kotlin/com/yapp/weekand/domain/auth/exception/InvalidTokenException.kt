package com.yapp.weekand.domain.auth.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.exception.BaseException

class InvalidTokenException: BaseException(ErrorCode.INVALID_REFRESH_JWT)
