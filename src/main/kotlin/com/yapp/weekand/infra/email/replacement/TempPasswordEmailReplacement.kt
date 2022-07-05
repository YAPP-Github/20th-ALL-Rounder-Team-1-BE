package com.yapp.weekand.infra.email.replacement

data class TempPasswordEmailReplacement(
	override val replacements: Map<String, String>
): EmailReplacement(replacements) {
	override val replacementKeys = arrayListOf("userEmail", "tempPassword")
	override val templateName = "temp_password_mail"
	override val title = "임시 비밀번호 안내"
}
