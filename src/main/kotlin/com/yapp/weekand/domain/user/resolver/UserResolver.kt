package com.yapp.weekand.domain.user.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.PaginationInfo
import com.yapp.weekand.api.generated.types.SearchUserList
import com.yapp.weekand.api.generated.types.SearchUserSort
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.user.service.UserService
import java.util.Random
import com.yapp.weekand.api.generated.types.User as UserGraphql

@DgsComponent
class UserResolver(
	private val userService: UserService
) {
	@DgsQuery
	fun checkDuplicateNickname(@InputArgument nickname: String): Boolean {
		return userService.checkDuplicateNickname(nickname)
	}

	@DgsQuery
	fun user(@InputArgument id: String): User? {
		return userService.findUserById(id.toLong())
	}

	fun tmpUserListGen(count:Int): List<UserGraphql> = IntArray(count) { it + 1}.map {
		UserGraphql(
			id = it.toString(),
			email = "user_${it}_email@email.com",
			nickname = "user_${it}",
			profileUrl = "https://user_${it}_profile_image.png",
			goal = "유저 ${it} 의 목표 입니다~",
			jobs = listOf("student", "nurse"),
			interests = listOf("취미1", "취미2"),
			followerCount = Random().nextInt() % 1000,
			followeeCount = Random().nextInt() % 1000,
		)
	}
	@DgsQuery
	fun searchUsers(@InputArgument searchQuery: String?, job: String?, interest: String?, sort: SearchUserSort?): SearchUserList {
		return SearchUserList(
			paginationInfo = PaginationInfo(hasNext = false),
			users = tmpUserListGen(20),
		)
	}
}
