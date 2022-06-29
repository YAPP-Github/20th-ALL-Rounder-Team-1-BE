package com.yapp.weekand.common.util

import java.util.regex.Pattern

object ValidationRegex {
	private val EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
	private val PASSWORD_REGEX = Pattern.compile("""^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$""")

	fun isRegexEmail(email: String): Boolean {
		return EMAIL_REGEX.matcher(email).matches()
	}

	fun isRegexPassword(password: String): Boolean {
		return PASSWORD_REGEX.matcher(password).matches()
	}
}
