package com.yapp.weekand.infra.email.replacement

abstract class EmailReplacement(
	open val replacements: Map<String, String>
) {
	abstract val replacementKeys: List<String>
	abstract val templateName: String
	abstract val title: String
	fun validate(): Boolean {
		return replacementKeys.fold(true) { result, key -> result && replacements.containsKey(key) }
	}
}
