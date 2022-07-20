package com.yapp.weekand.domain.follow.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class FollowNotFoundException: AbstractBaseGraphQLException(ErrorCode.FOLLOW_NOT_FOUND)
