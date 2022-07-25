package com.yapp.weekand.domain.schedule.mapper

import com.yapp.weekand.domain.category.mapper.toGraphql
import com.yapp.weekand.api.generated.types.ScheduleRule as ScheduleGraphql
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.entity.Status

fun ScheduleRule.toScheduleRuleGraphql() =
	ScheduleGraphql(
		id = id.toString(),
		name = name,
		category = scheduleCategory.toGraphql(),
		dateTimeStart = dateStart,
		dateTimeEnd = dateEnd,
		repeatType = repeatType,
		repeatSelectedValue = repeatSelectedValue,
		memo = memo
	)
