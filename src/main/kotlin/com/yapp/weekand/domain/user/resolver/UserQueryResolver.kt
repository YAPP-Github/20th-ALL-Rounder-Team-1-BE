package com.yapp.weekand.domain.user.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.PaginationInfo
import com.yapp.weekand.api.generated.types.SearchUserList
import com.yapp.weekand.api.generated.types.SearchUserSort
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.user.mapper.toGraphql
import com.yapp.weekand.domain.user.service.UserService
import com.yapp.weekand.api.generated.types.User as UserGraphql

@DgsComponent
class UserQueryResolver(
	private val userService: UserService
) {
	@DgsQuery
	fun checkDuplicateNickname(@InputArgument nickname: String): Boolean {
		return userService.checkDuplicateNickname(nickname)
	}

	@DgsQuery
	@JwtAuth
	fun user(): UserGraphql? {
		val currentUser = userService.getCurrentUser()
		return currentUser.toGraphql()
	}

	@DgsQuery
	@JwtAuth
	fun searchUsers(
		@InputArgument searchQuery: String?,
		@InputArgument jobs: List<String>?,
		@InputArgument interests: List<String>?,
		@InputArgument sort: SearchUserSort?
	): SearchUserList {
		return SearchUserList(
			paginationInfo = PaginationInfo(hasNext = false),
			users = userService.searchUsers(searchQuery, jobs, interests, sort).map { it.toGraphql() },
		)
	}
}
