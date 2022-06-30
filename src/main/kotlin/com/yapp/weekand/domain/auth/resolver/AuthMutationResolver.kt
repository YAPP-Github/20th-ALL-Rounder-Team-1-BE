package com.yapp.weekand.domain.auth.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.PasswordInput
import com.yapp.weekand.api.generated.types.SignUpInput
import com.yapp.weekand.common.util.ValidationRegex.isRegexEmail
import com.yapp.weekand.common.util.ValidationRegex.isRegexPassword
import com.yapp.weekand.domain.auth.exception.InvalidEmailException
import com.yapp.weekand.domain.auth.exception.InvalidNicknameException
import com.yapp.weekand.domain.auth.exception.InvalidPasswordException
import com.yapp.weekand.domain.auth.exception.SignUpFailException
import com.yapp.weekand.domain.auth.service.AuthService
import com.yapp.weekand.domain.user.service.UserService

@DgsComponent
class AuthMutationResolver(
	private val authService: AuthService,
	private val userService: UserService
) {
	@DgsMutation
	fun signUp(@InputArgument signUpInput: SignUpInput): Boolean {
		if(!signUpInput.signUpAgreed) {
			throw SignUpFailException()
		}

		if(!isRegexEmail(signUpInput.email)) {
			throw InvalidEmailException()
		}

		if(!isRegexPassword(signUpInput.password)) {
			throw InvalidPasswordException()
		}

		if(MAX_NICKNAME_LENGTH < signUpInput.nickname.length || signUpInput.nickname.length < MIN_NICKNAME_LENGTH) {
			throw InvalidNicknameException()
		}
		authService.signUp(signUpInput)
		return true
	}

	@DgsMutation
	fun updatePassword(@InputArgument passwordInput: PasswordInput): Boolean {
		if(!isRegexPassword(passwordInput.newPassword)) {
			throw InvalidPasswordException()
		}
		authService.updatePassword(userService.getCurrentUser(), passwordInput)
		return true
	}

	companion object {
		const val MIN_NICKNAME_LENGTH = 2
		const val MAX_NICKNAME_LENGTH = 12
	}
}
