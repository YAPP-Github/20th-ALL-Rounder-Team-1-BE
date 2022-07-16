package com.yapp.weekand.domain.user.repository

import com.yapp.weekand.domain.user.dto.SearchUserListCondition
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRepositorySupport {
	fun searchUserListWithPaging(
		condition: SearchUserListCondition,
		pageable: Pageable
	): Page<User>
}
