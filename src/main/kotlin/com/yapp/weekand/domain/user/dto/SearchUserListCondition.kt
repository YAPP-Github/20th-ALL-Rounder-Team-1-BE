package com.yapp.weekand.domain.user.dto

import com.yapp.weekand.api.generated.types.SearchUserSort

data class SearchUserListCondition(
	val nickNameOrGoalQuery: String?,
	val jobs: List<String>?,
	val interests: List<String>?,
	val sort: SearchUserSort?
)
