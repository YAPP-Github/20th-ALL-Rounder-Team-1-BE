package com.yapp.weekand.common.error.exception

import com.yapp.weekand.common.error.ErrorCode

open class BaseException(
	open val errorCode: ErrorCode
): RuntimeException(errorCode.message)
