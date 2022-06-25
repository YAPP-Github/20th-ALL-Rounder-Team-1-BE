package com.yapp.weekand.domain.schedule.mapper


import com.yapp.weekand.api.generated.types.ScheduleCategory as ScheduleCategoryGraphql
import com.yapp.weekand.domain.category.entity.ScheduleCategory

fun ScheduleCategory.toGraphql() =
	ScheduleCategoryGraphql(
		id = id.toString(),
		name = name,
		color = color,
		openType = openType
	)
