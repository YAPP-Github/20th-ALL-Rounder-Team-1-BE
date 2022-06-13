package com.yapp.weekand.graphql

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class WeekandResolver {
	private val defaultWeekand = Weekand("world")

	@DgsQuery
	fun weekand(@InputArgument customField: String?): Weekand {
		if (customField === "error") {
			throw WeekandException("현재 weekand 이용이 불가능 합니다")
		}
		if (customField === null) {
			return defaultWeekand
		}
		return Weekand(customField)
	}

	data class Weekand(val hello: String)
}
