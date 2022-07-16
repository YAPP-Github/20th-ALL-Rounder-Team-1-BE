package com.yapp.weekand.domain.category.repository

import com.yapp.weekand.domain.category.entity.ScheduleCategory
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleCategoryRepository: JpaRepository<ScheduleCategory, Long> {
	fun findByUser(user: User, pageable: Pageable): Slice<ScheduleCategory>
	fun existsByName(name: String): Boolean
}
