package com.yapp.weekand.domain.auth.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.exception.BaseException

class EmailFailException: BaseException(ErrorCode.EMAIL_SEND_FAIL)
