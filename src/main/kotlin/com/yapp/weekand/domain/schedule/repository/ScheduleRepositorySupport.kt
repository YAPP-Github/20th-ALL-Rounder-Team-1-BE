package com.yapp.weekand.domain.schedule.repository

import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ScheduleRepositorySupport {
	fun getUserSchedulesByDate(dateYmd: String, userId: Long, currentUserId: Long): List<ScheduleRule>
}
