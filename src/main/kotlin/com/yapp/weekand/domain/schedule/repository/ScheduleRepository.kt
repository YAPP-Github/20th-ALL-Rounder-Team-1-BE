package com.yapp.weekand.domain.schedule.repository

import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleRepository : JpaRepository<ScheduleRule, Long> {
}
