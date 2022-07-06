package com.yapp.weekand.infra.email.replacement

data class AuthEmailReplacement(
	override val replacements: Map<String, String>
): EmailReplacement(replacements) {
	override val replacementKeys = arrayListOf("code")
	override val templateName = "auth_mail"
	override val title = "인증번호 안내"
}
