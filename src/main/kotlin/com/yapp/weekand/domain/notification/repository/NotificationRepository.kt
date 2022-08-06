package com.yapp.weekand.domain.notification.repository

import com.yapp.weekand.domain.notification.entity.Notification
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository: JpaRepository<Notification, Long> {
	fun findByUserOrderByDateCreatedDesc(user: User, pageable: Pageable): Slice<Notification>

	fun findByUser(user: User): List<Notification>
}
