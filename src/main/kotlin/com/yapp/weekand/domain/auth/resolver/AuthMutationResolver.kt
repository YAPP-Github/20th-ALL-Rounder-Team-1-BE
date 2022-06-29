package com.yapp.weekand.domain.auth.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.yapp.weekand.api.generated.types.SignUpInput
import com.yapp.weekand.common.util.ValidationRegex.isRegexEmail
import com.yapp.weekand.common.util.ValidationRegex.isRegexPassword
import com.yapp.weekand.domain.auth.exception.InvalidFormattedException
import com.yapp.weekand.domain.auth.exception.SignUpFailException
import com.yapp.weekand.domain.auth.service.AuthService

@DgsComponent
class AuthMutationResolver(
	private val authService: AuthService
) {
	@DgsMutation
	fun signUp(signUpInput: SignUpInput): String {
		if(signUpInput.marketingAgreed == false) {
			throw SignUpFailException()
		}

		if(!isRegexEmail(signUpInput.email)) {
			throw InvalidFormattedException()
		}

		if(!isRegexPassword(signUpInput.password)) {
			throw InvalidFormattedException()
		}

		if(MAX_NICKNAME_LENGTH < signUpInput.nickName.length || signUpInput.nickName.length < MIN_NICKNAME_LENGTH) {
			throw InvalidFormattedException()
		}
		return authService.signUp(signUpInput)
	}

	companion object {
		const val MIN_NICKNAME_LENGTH = 2
		const val MAX_NICKNAME_LENGTH = 12
	}
}
