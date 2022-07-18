package com.yapp.weekand.common.util

import java.util.regex.Pattern

object ValidationRegex {
	private val EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
	//숫자, 영어 조합 8자리 이상
	private val PASSWORD_REGEX = Pattern.compile("^(?=.*\\d)(?=.*[a-z])[0-9a-zA-Z!@#${'$'}%^&*()\\-_=+{}|?>.<,:;~`']{8,}${'$'}")

	fun isRegexEmail(email: String): Boolean {
		return EMAIL_REGEX.matcher(email).matches()
	}

	fun isRegexPassword(password: String): Boolean {
		return PASSWORD_REGEX.matcher(password).matches()
	}
}
