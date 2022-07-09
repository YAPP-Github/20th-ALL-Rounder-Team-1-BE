package com.yapp.weekand.domain.schedule.repository

import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.entity.ScheduleStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ScheduleStatusRepository : JpaRepository<ScheduleStatus, Long> {
	fun existsByDateYmdAndScheduleRule(date: LocalDate, scheduleRule: ScheduleRule): Boolean
}
