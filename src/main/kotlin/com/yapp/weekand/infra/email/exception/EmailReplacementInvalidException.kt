package com.yapp.weekand.infra.email.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class EmailReplacementInvalidException: AbstractBaseGraphQLException(ErrorCode.EMAIL_REPLACEMENT_INVALID)
