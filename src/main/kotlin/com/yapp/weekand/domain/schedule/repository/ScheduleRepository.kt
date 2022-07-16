package com.yapp.weekand.domain.schedule.repository

import com.yapp.weekand.domain.category.entity.ScheduleCategory
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface ScheduleRepository : JpaRepository<ScheduleRule, Long> {
	@Query("SELECT r FROM ScheduleRule r JOIN FETCH r.scheduleCategory WHERE r.scheduleCategory.id = :id AND r.name LIKE CONCAT('%', :searchQuery, '%')")
	fun searchScheduleRules(pageable: Pageable, @Param("searchQuery") searchQuery: String?, @Param("id") scheduleId: Long): Slice<ScheduleRule>

	@Query("SELECT r FROM ScheduleRule r JOIN FETCH r.scheduleCategory WHERE r.scheduleCategory.id = :id")
	fun findScheduleRules(pageable: Pageable, @Param("id") scheduleId: Long): Slice<ScheduleRule>

	@Query("SELECT r FROM ScheduleRule r JOIN FETCH r.scheduleCategory LEFT JOIN FETCH r.scheduleStatus WHERE r.id = :id")
	fun findByScheduleId(@Param("id") scheduleId: Long): ScheduleRule?

	fun findByScheduleCategory(scheduleCategory: ScheduleCategory): List<ScheduleRule>
}
