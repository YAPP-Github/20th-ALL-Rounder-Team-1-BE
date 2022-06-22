package com.yapp.weekand.domain.sticker.service

import com.yapp.weekand.api.generated.types.ScheduleSticker
import com.yapp.weekand.api.generated.types.ScheduleStickerSummary
import com.yapp.weekand.api.generated.types.ScheduleStickerUser
import com.yapp.weekand.domain.schedule.exception.ScheduleNotFoundException
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import com.yapp.weekand.domain.sticker.repository.ScheduleStickerRepository
import com.yapp.weekand.domain.user.mapper.toGraphql
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class ScheduleStickerService(
	private val scheduleStickerRepository: ScheduleStickerRepository,
	private val scheduleRepository: ScheduleRepository,
) {
	fun getScheduleStickerSummary(scheduleId: Long): ScheduleStickerSummary {
		val schedule =
			scheduleRepository.findByIdOrNull(scheduleId) ?: throw ScheduleNotFoundException()

		// TODO: date 관련 Graphql scalar 적용 후 인자를 받아 적용
		val dateTime = LocalDate.now();
		val scheduleStickerList =
			scheduleStickerRepository.findByScheduleRuleAnAndScheduleDateOrderByDateCreatedDesc(schedule, dateTime)

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

		return ScheduleStickerSummary(
			totalCount = scheduleStickerList.size,
			scheduleStickers,
			scheduleStickerUsers,
		)
	}
}
