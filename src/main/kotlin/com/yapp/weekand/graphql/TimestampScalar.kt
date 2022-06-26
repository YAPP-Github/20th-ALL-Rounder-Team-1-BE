package com.yapp.weekand.graphql

import com.netflix.graphql.dgs.DgsScalar
import graphql.schema.Coercing
import java.time.*

@DgsScalar(name = "Timestamp")
class TimestampScalar: Coercing<LocalDateTime, Long> {
	override fun serialize(dataFetcherResult: Any): Long {
		dataFetcherResult as LocalDateTime
		return dataFetcherResult.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli()
	}

	override fun parseValue(input: Any): LocalDateTime {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(input as Long), ZoneId.of("Asia/Seoul"));
	}

	override fun parseLiteral(input: Any): LocalDateTime {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(input.toString().toLong()), ZoneId.of("Asia/Seoul"));
	}
}
