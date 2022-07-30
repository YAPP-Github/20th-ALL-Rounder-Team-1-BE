package com.yapp.weekand.domain.sticker.service

import com.yapp.weekand.api.generated.types.*
import com.yapp.weekand.domain.notification.entity.NotificationType
import com.yapp.weekand.domain.notification.service.NotificationService
import com.yapp.weekand.domain.schedule.exception.ScheduleNotFoundException
import com.yapp.weekand.domain.sticker.entity.ScheduleSticker
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import com.yapp.weekand.domain.sticker.repository.ScheduleStickerRepository
import com.yapp.weekand.domain.user.mapper.toGraphql
import com.yapp.weekand.domain.user.service.UserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class ScheduleStickerService(
	private val scheduleStickerRepository: ScheduleStickerRepository,
	private val scheduleRepository: ScheduleRepository,
	private val userService: UserService,
	private val notificationService: NotificationService
) {
	fun getScheduleStickerSummary(scheduleId: Long, date: LocalDateTime): ScheduleStickerSummary {
		val schedule =
			scheduleRepository.findByIdOrNull(scheduleId) ?: throw ScheduleNotFoundException()
		val user = userService.getCurrentUser()
		val dateTime = date.toLocalDate()
		val scheduleStickerList =
			scheduleStickerRepository.findByScheduleRuleAndScheduleDateOrderByDateCreatedDesc(schedule, dateTime)

		val scheduleStickers = scheduleStickerList
			.groupingBy { it.name }
			.eachCount()
			.map {
				ScheduleSticker(
					name = it.key,
					stickerCount = it.value
				)
			}
		val scheduleStickerUsers = scheduleStickerList
			.map {
				ScheduleStickerUser(
					user = it.user.toGraphql(),
					stickerName = it.name,
				)
			}
		val myScheduleSticker = scheduleStickerUsers.find { it.user == user.toGraphql() }

		return ScheduleStickerSummary(
			totalCount = scheduleStickerList.size,
			scheduleStickers = scheduleStickers,
			scheduleStickerUsers = scheduleStickerUsers,
			myScheduleSticker = myScheduleSticker
		)
	}

	@Transactional
	fun createScheduleSticker(input: CreateScheduleStickerInput, user: User) {
		val schedule = scheduleRepository.findByIdOrNull(input.scheduleId.toLong())
			?: throw ScheduleNotFoundException()

		val scheduleSticker = scheduleStickerRepository.findByUserAndScheduleRuleAndScheduleDate(
			user,
			schedule,
			input.scheduleDate.toLocalDate()
		)
		if (scheduleSticker != null) {
			scheduleStickerRepository.delete(scheduleSticker)
		}

		scheduleStickerRepository.save(
			ScheduleSticker(
				name = input.scheduleStickerName,
				user = user,
				scheduleRule = schedule,
				scheduleDate = input.scheduleDate.toLocalDate()
			)
		)

		val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
		val dateYmd = input.scheduleDate.format(formatter)
		val notiMessage = "${user.nickname}님이 ${dateYmd} ${schedule.name} 일정에 스티커를 붙였습니다"

		val tagetUser = schedule.user
		notificationService.addNotifications(tagetUser, NotificationType.STICKER, notiMessage)
	}

	@Transactional
	fun deleteScheduleSticker(input: DeleteScheduleStickerInput) {
		val schedule = scheduleRepository.findByIdOrNull(input.scheduleId.toLong())
			?: throw ScheduleNotFoundException()
		val user = userService.getCurrentUser()

		scheduleStickerRepository.deleteByUserAndScheduleRuleAndScheduleDate(
			user,
			schedule,
			input.scheduleDate.toLocalDate()
		)
	}

	@Transactional
	fun deleteScheduleStickerByUser(user: User) {
		val scheduleSticker = scheduleStickerRepository.findByUser(user)
		scheduleStickerRepository.deleteAllInBatch(scheduleSticker)
	}
}
