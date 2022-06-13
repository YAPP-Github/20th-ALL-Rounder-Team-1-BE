package com.yapp.weekand.common.error.graphql

import com.yapp.weekand.common.util.Logger
import graphql.GraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class GraphQLConfig: DataFetcherExceptionHandler {
	override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
		val exception = handlerParameters.exception
		val sourceLocation = handlerParameters.sourceLocation
		val path = handlerParameters.path

		val error: GraphQLError =
			SimpleGraphQLError(
				exception = exception,
				locations = listOf(sourceLocation),
				path = path.toList()
			).also {
				if (exception is WeekandError) {
					it.addExtension("code", exception.code)
				}
			}

		Logger.error(error.message)

		val result = DataFetcherExceptionHandlerResult.newResult(error).build()
		return CompletableFuture.completedFuture(result)
	}
}
