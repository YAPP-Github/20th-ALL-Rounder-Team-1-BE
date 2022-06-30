package com.yapp.weekand.common.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ValidationRegexTest {
	@Test
	fun `이메일 정규식 성공`() {
	    val email = "test@gmail.com"
		Assertions.assertThat(ValidationRegex.isRegexEmail(email)).isTrue
	}

	@Test
	fun `이메일 정규식 실패`() {
		val email1 = "test"
		val email2 = "test@test"
		Assertions.assertThat(ValidationRegex.isRegexEmail(email1)).isFalse
		Assertions.assertThat(ValidationRegex.isRegexEmail(email2)).isFalse
	}

	@Test
	fun `비밀번호 정규식 성공`() {
		val password = "abcdefe1"
		Assertions.assertThat(ValidationRegex.isRegexPassword(password)).isTrue
	}

	@Test
	fun `비밀번호 정규식 실패`() {
		val password1 = "abcdefge"
		val password2 = "abc123"
		val password3 = "12345678"
		Assertions.assertThat(ValidationRegex.isRegexPassword(password1)).isFalse
		Assertions.assertThat(ValidationRegex.isRegexPassword(password2)).isFalse
		Assertions.assertThat(ValidationRegex.isRegexPassword(password3)).isFalse
	}
}
