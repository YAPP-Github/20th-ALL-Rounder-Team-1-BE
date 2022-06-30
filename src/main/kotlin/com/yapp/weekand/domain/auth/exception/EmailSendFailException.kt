package com.yapp.weekand.domain.auth.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class EmailSendFailException: AbstractBaseGraphQLException(ErrorCode.EMAIL_SEND_FAIL)
