package com.yapp.weekand.common.error.graphql

import graphql.ErrorClassification
import graphql.ErrorType
import graphql.GraphQLError
import graphql.language.SourceLocation

open class SimpleGraphQLError(
	private val exception: Throwable,
	private val locations: List<SourceLocation>? = null,
	private val path: List<Any>? = null,
	private val errorType: ErrorClassification = ErrorType.DataFetchingException
) : GraphQLError {

	private val extensions: MutableMap<String, Any> = mutableMapOf()

	fun addExtension(key: String, value: Any) = extensions.put(key, value)

	override fun getErrorType(): ErrorClassification = errorType

	override fun getExtensions(): Map<String, Any> =
		if (exception is GraphQLError && exception.extensions != null) {
			mutableMapOf<String, Any>().apply {
				putAll(exception.extensions)
				putAll(extensions)
			}.toMap()
		} else {
			extensions
		}

	override fun getLocations(): List<SourceLocation>? = locations

	override fun getMessage(): String = if(exception.message != null) exception.message.orEmpty() else "unknown error"

	override fun getPath(): List<Any>? = path
}
