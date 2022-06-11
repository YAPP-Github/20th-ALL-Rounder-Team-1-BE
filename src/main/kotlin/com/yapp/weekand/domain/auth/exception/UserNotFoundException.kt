package com.yapp.weekand.domain.auth.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.exception.BaseException

class UserNotFoundException: BaseException(ErrorCode.USER_NOT_FOUND)
