package com.yapp.weekand.common.entity

import com.yapp.weekand.api.generated.types.CreateScheduleStickerInput
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.sticker.entity.ScheduleSticker
import com.yapp.weekand.domain.sticker.entity.ScheduleStickerName
import java.time.LocalDate
import java.time.LocalDateTime

class ScheduleStickerFactory {
	companion object {
		fun scheduleStickerList(size: Int) = IntArray(size) { it + 1 }.map {
			ScheduleSticker(
				id = it.toLong(),
				user = UserFactory.testLoginUser(),
				scheduleRule = ScheduleRuleFactory.scheduleRule(),
				name = ScheduleStickerName.CHEER_UP,
				scheduleDate = LocalDate.now(),
			)
		}

		fun scheduleStickerInput() =
			CreateScheduleStickerInput(
				scheduleId = 1.toString(),
				scheduleStickerName = ScheduleStickerName.CHEER_UP,
				scheduleDate = LocalDateTime.now()
			)

		fun scheduleSticker(scheduleRule: ScheduleRule, scheduleStickerName: ScheduleStickerName) =
			ScheduleSticker(
				id = 2L,
				user = UserFactory.testLoginUser(),
				scheduleRule = scheduleRule,
				name = scheduleStickerName,
				scheduleDate = LocalDate.now()
			)
	}
}
