package com.yapp.weekand.domain.sticker.repository

import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.sticker.entity.ScheduleSticker
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ScheduleStickerRepository : JpaRepository<ScheduleSticker, Long> {
	fun findByScheduleRuleAnAndScheduleDateOrderByDateCreatedDesc(scheduleRule: ScheduleRule, scheduleDate: LocalDate): List<ScheduleSticker>
}
