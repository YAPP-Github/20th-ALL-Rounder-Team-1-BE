package com.yapp.weekand.domain.notification.resolver

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.yapp.weekand.api.generated.types.NotificationList
import com.yapp.weekand.api.generated.types.PaginationInfo
import com.yapp.weekand.domain.notification.service.NotificationService
import com.yapp.weekand.domain.user.service.UserService
import org.springframework.data.domain.PageRequest

@DgsComponent
class NotificationResolver (
	private val notificationService: NotificationService,
	private val userService: UserService
) {
	@DgsQuery
	fun notifications(@InputArgument page: Int, @InputArgument size: Int): NotificationList {
		val notifications = notificationService.getNotifications(userService.getCurrentUser(), PageRequest.of(page, size))
		return NotificationList(paginationInfo = PaginationInfo(notifications.hasNext()), notifications = notifications.content)

	}
}
