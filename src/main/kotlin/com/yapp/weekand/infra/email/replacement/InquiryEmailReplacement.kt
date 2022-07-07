package com.yapp.weekand.infra.email.replacement

data class InquiryEmailReplacement (
	override val replacements: Map<String, String>,
	override val title: String
): EmailReplacement(replacements) {
	override val replacementKeys = arrayListOf("userEmail", "contents")
	override val templateName = "inquiry_mail"
}
