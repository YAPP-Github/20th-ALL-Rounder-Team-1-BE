package com.yapp.weekand.domain.notification.service

import com.yapp.weekand.api.generated.types.Notification as NotificationGraphqlType
import com.yapp.weekand.common.entity.UserFactory
import com.yapp.weekand.domain.notification.entity.Notification
import com.yapp.weekand.domain.notification.entity.NotificationType
import com.yapp.weekand.domain.notification.repository.NotificationRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl

@ExtendWith(MockKExtension::class)
internal class NotificationServiceTest {

	@InjectMockKs
	lateinit var notificationService: NotificationService

	@MockK(relaxed = true)
	lateinit var notificationRepository: NotificationRepository

	@Test
	fun `알림 목록을 조회해야 한다`() {
		val givenUser = UserFactory.testLoginUser()
		var givenPage = 1
		var givenSize = 5
		val givenPageable = PageRequest.of(givenPage, givenSize)

		notificationService.getNotifications(givenUser, givenPageable)

		verify(exactly = 1) {
			notificationRepository.findByUserOrderByDateCreatedDesc(givenUser, givenPageable)
		}
	}

	@Test
	fun `알림 목록을 반환해야 한다`() {
		val givenUser = UserFactory.testLoginUser()
		var givenPage = 1
		var givenSize = 5
		val givenPageable = PageRequest.of(givenPage, givenSize)

		val givenNotifications = SliceImpl(
			listOf(
				Notification(
					id = 1,
					user = givenUser,
					message = "알림 1",
					type = NotificationType.FOLLOW
				),
				Notification(
					id = 2,
					user = givenUser,
					message = "알림 2",
					type = NotificationType.SCHEDULE_END
				),
			)
		)

		every {
			notificationRepository.findByUserOrderByDateCreatedDesc(
				givenUser,
				givenPageable
			)
		} returns givenNotifications

		val result = notificationService.getNotifications(givenUser, givenPageable)

		val expectedNotifications = givenNotifications.map {
			NotificationGraphqlType(id = it.id.toString(), message = it.message, type = it.type)
		}

		assertEquals(result, expectedNotifications)
	}
}
