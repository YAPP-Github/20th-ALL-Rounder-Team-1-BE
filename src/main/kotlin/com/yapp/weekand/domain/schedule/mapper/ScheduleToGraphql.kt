package com.yapp.weekand.domain.schedule.mapper

import com.yapp.weekand.api.generated.types.ScheduleInfo
import com.yapp.weekand.domain.category.mapper.toGraphql
import com.yapp.weekand.api.generated.types.ScheduleRule as ScheduleGraphql
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.entity.Status
import java.time.LocalDateTime

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

fun ScheduleRule.toScheduleInfoGraphql(dateTimeStart: LocalDateTime, dateTimeEnd: LocalDateTime) =
	ScheduleInfo(
		id = id.toString(),
		name = name,
		category = scheduleCategory.toGraphql(),
		dateTimeStart = dateTimeStart,
		dateTimeEnd = dateTimeEnd,
		repeatType = repeatType,
		repeatSelectedValue = repeatSelectedValue,
		memo = memo,
		dateSkip = scheduleStatus
			.filter { it.status == Status.SKIP }
			.map { it.dateYmd.atStartOfDay() },
		status = Status.UNDETERMINED
	)
