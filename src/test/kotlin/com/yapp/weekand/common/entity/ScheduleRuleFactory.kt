package com.yapp.weekand.common.entity

import com.yapp.weekand.domain.category.entity.ScheduleCategory
import com.yapp.weekand.domain.category.entity.ScheduleCategoryOpenType
import com.yapp.weekand.domain.schedule.entity.RepeatType
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import java.time.LocalDateTime

class ScheduleRuleFactory {
	companion object {
		fun scheduleRule() =
			ScheduleRule(
				id = 1L,
				name = "나의 스케줄",
				dateStart = LocalDateTime.now(),
				dateEnd = LocalDateTime.now(),
				dateRepeatEnd = LocalDateTime.now(),
				memo = "나의 스케줄 메모 입니다",
				repeatType = RepeatType.ONCE,
				repeatSelectedValue = null,
				user = UserFactory.testLoginUser(),
				scheduleCategory = ScheduleCategory(
					id = 11L,
					name = "스케줄 카테고리",
					color = "RED",
					openType = ScheduleCategoryOpenType.ALL_OPEN,
					user = UserFactory.testLoginUser(),
				)
			)
	}
}
