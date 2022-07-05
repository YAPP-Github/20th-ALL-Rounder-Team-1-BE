package com.yapp.weekand.infra.email.replacement

data class TempPasswordEmailReplacement(
	val replacements: Map<String, String>
): EmailReplacement(replacements) {
	override val replacementKeys = arrayListOf("userEmail", "tempPassword")
	override val templateName = "temp_password_mail"
}
