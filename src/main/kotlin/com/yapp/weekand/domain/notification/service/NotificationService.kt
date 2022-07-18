package com.yapp.weekand.domain.notification.service

import com.yapp.weekand.api.generated.types.Notification
import com.yapp.weekand.domain.notification.repository.NotificationRepository
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NotificationService (
	private val notificationRepository: NotificationRepository
) {
	fun getNotifications(user: User, pageable: Pageable): Slice<Notification> =
		notificationRepository.findByUserOrderByDateCreatedDesc(user, pageable)
			.map{ Notification(id= it.id.toString(), message = it.message, type = it.type)}

	@Transactional
	fun deleteNotificationByUser(user: User) {
		val notifications = notificationRepository.findByUser(user)
		notificationRepository.deleteAllInBatch(notifications)
	}
}
