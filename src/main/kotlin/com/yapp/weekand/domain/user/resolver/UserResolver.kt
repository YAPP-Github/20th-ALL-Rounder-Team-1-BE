package com.yapp.weekand.domain.user.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.domain.user.service.UserService

@DgsComponent
class UserResolver(
	private val userService: UserService
) {
	@DgsQuery
	fun checkDuplicateNickname(@InputArgument nickname : String):Boolean {
		return userService.checkDuplicateNickname(nickname)
	}
}
