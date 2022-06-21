package com.yapp.weekand.common.entity

import com.yapp.weekand.domain.sticker.entity.ScheduleSticker
import com.yapp.weekand.domain.sticker.entity.ScheduleStickerName
import java.time.LocalDate

class ScheduleStickerFactory {
	companion object {
		fun scheduleStickerList(size: Int) = IntArray(size) { it + 1 }.map {
			ScheduleSticker(
				id = it.toLong(),
				user = EntityFactory.testLoginUser(),
				scheduleRule = ScheduleRuleFactory.scheduleRule(),
				name = ScheduleStickerName.CHEER_UP,
				scheduleDate = LocalDate.now(),
			)
		}
	}
}
