package com.yapp.weekand.infra.s3.exception

import com.yapp.weekand.common.error.ErrorCode
import com.yapp.weekand.common.error.graphql.AbstractBaseGraphQLException

class FileUploadFailException: AbstractBaseGraphQLException(ErrorCode.FILE_UPLOAD_FAIL)
