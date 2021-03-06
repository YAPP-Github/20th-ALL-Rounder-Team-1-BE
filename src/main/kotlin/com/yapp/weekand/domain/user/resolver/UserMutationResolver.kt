package com.yapp.weekand.domain.user.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.CreateUserProfileImageS3PresignedUrlInput
import com.yapp.weekand.api.generated.types.UpdateUserProfileInput
import com.yapp.weekand.api.generated.types.User
import com.yapp.weekand.api.generated.types.UserProfileImageS3PresignedUrl
import com.yapp.weekand.common.jwt.aop.JwtAuth
import com.yapp.weekand.domain.user.mapper.toGraphql
import com.yapp.weekand.domain.user.service.UserService
import com.yapp.weekand.domain.user.service.UserWithdrawalService

@DgsComponent
class UserMutationResolver(
	private val userService: UserService,
	private val userWithdrawalService: UserWithdrawalService,
	) {
	@DgsMutation
	@JwtAuth
	fun inquiry(@InputArgument contents: String): Boolean {
		userService.sendInquiryMail(userService.getCurrentUser(), contents)
		return true
	}

	@DgsMutation
	@JwtAuth
	fun updateUserProfile(@InputArgument input: UpdateUserProfileInput): User {
		val userId = userService.getCurrentUser().id
		userService.updateUserProfile(userId, input)
		return userService.findUserById(userId)!!.toGraphql()
	}

	@DgsMutation
	@JwtAuth
	fun createUserProfileImageS3PresignedUrl(@InputArgument input: CreateUserProfileImageS3PresignedUrlInput): UserProfileImageS3PresignedUrl {
		return userService.createUserProfileImageS3PresignedUrl(userService.getCurrentUser().id, input.extension)
	}

	@DgsMutation
	@JwtAuth
	fun deleteUser(): Boolean {
		userWithdrawalService.withDrawUser(userService.getCurrentUser().id)
		return true
	}
}
