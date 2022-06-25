package com.yapp.weekand.domain.schedule.repository

import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface ScheduleRepository : JpaRepository<ScheduleRule, Long> {
	@Query("SELECT r FROM ScheduleRule r join fetch r.scheduleCategory where r.scheduleCategory.id = :id and r.name like concat('%', :searchQuery, '%')")
	fun searchScheduleRules(pageable: Pageable, @Param("searchQuery") searchQuery: String?, @Param("id") scheduleId: Long): Slice<ScheduleRule>

	@Query("SELECT r FROM ScheduleRule r join fetch r.scheduleCategory where r.scheduleCategory.id = :id")
	fun findScheduleRules(pageable: Pageable, @Param("id") scheduleId: Long): Slice<ScheduleRule>
}
