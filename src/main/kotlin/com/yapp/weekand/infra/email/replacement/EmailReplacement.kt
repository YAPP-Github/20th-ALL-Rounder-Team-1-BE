package com.yapp.weekand.infra.email.replacement

abstract class EmailReplacement(
	private val replacements: Map<String, String>
) {
	abstract val replacementKeys: List<String>
	abstract val templateName: String
	fun validate(): Boolean {
		return replacementKeys.fold(true) { result, key -> result && replacements.containsKey(key) }
	}
}
