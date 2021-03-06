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
import org.springframework.data.domain.PageRequest
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
	fun user(@InputArgument id: String?): UserGraphql? {
		if (id == null) {
			val currentUser = userService.getCurrentUser()
			return currentUser.toGraphql()
		}
		val targetUser = userService.findUserById(id.toLong())
		return targetUser?.toGraphql()
	}

	@DgsQuery
	@JwtAuth
	fun searchUsers(
		@InputArgument searchQuery: String?,
		@InputArgument jobs: List<String>?,
		@InputArgument interests: List<String>?,
		@InputArgument sort: SearchUserSort?,
		@InputArgument page: Int,
		@InputArgument size: Int,
	): SearchUserList {
		val users = userService.searchUsers(searchQuery, jobs, interests, sort, PageRequest.of(page, size))
		return SearchUserList(
			paginationInfo = PaginationInfo(hasNext = users.hasNext()),
			users = users.content.map { it.toGraphql() },
		)
	}
}
