package com.yapp.weekand.domain.user.service

import com.yapp.weekand.domain.auth.exception.UserNotFoundException
import com.yapp.weekand.domain.category.service.ScheduleCategoryService
import com.yapp.weekand.domain.follow.service.FollowService
import com.yapp.weekand.domain.interest.service.InterestService
import com.yapp.weekand.domain.job.service.JobService
import com.yapp.weekand.domain.notification.service.NotificationService
import com.yapp.weekand.domain.schedule.service.ScheduleService
import com.yapp.weekand.domain.sticker.service.ScheduleStickerService
import com.yapp.weekand.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserWithdrawalService(
	private val categoryService: ScheduleCategoryService,
	private val followService: FollowService,
	private val interestService: InterestService,
	private val jobService: JobService,
	private val notificationService: NotificationService,
	private val scheduleService: ScheduleService,
	private val stickerService: ScheduleStickerService,
	private val userRepository: UserRepository,
) {

	@Transactional
	fun withDrawUser(userId: Long) {
		val targetUser = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

		categoryService.deleteCategoryByUser(targetUser)
		followService.deleteFollowByUser(targetUser)
		interestService.deleteInterestByUser(targetUser)
		jobService.deleteJobByUser(targetUser)
		notificationService.deleteNotificationByUser(targetUser)
		scheduleService.deleteScheduleByUser(targetUser)
		stickerService.deleteScheduleStickerByUser(targetUser)

		userRepository.delete(targetUser)
	}
}
