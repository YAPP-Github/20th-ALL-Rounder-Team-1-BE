package com.yapp.weekand.domain.sticker.repository

import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.sticker.entity.ScheduleSticker
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ScheduleStickerRepository : JpaRepository<ScheduleSticker, Long> {
	fun findByScheduleRuleAndScheduleDateOrderByDateCreatedDesc(scheduleRule: ScheduleRule, scheduleDate: LocalDate): List<ScheduleSticker>
	fun findByUserAndScheduleRuleAndScheduleDate(user: User, scheduleRule: ScheduleRule, scheduleDate: LocalDate): ScheduleSticker?
}
