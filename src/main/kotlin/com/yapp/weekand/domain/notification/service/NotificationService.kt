package com.yapp.weekand.domain.notification.service

import com.yapp.weekand.domain.notification.entity.Notification
import com.yapp.weekand.domain.notification.entity.NotificationType
import com.yapp.weekand.domain.notification.repository.NotificationRepository
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.yapp.weekand.api.generated.types.Notification as NotificationGraphql

@Service
@Transactional(readOnly = true)
class NotificationService(
	private val notificationRepository: NotificationRepository
) {
	fun getNotifications(user: User, pageable: Pageable): Slice<NotificationGraphql> =
		notificationRepository.findByUserOrderByDateCreatedDesc(user, pageable)
			.map { NotificationGraphql(id = it.id.toString(), message = it.message, type = it.type) }

	@Transactional
	fun addNotifications(user: User, type: NotificationType, message: String) {
		notificationRepository.save(
			Notification(
				user = user,
				message = message,
				type = type
			)
		)
	}

	@Transactional
	fun deleteNotificationByUser(user: User) {
		val notifications = notificationRepository.findByUser(user)
		notificationRepository.deleteAllInBatch(notifications)
	}
}
