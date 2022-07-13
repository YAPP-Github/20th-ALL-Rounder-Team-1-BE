package com.yapp.weekand.domain.user.repository

import com.yapp.weekand.api.generated.types.SearchUserSort
import com.yapp.weekand.domain.user.entity.User

interface UserRepositorySupport {
	fun searchUserList(
		nickNameOrGoalQuery: String?,
		job: List<String>?,
		interest: List<String>?,
		sort: SearchUserSort?
	): List<User>
}
