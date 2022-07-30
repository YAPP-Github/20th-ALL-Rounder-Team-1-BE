package com.yapp.weekand.domain.schedule.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.weekand.api.generated.types.Week
import com.yapp.weekand.domain.category.entity.ScheduleCategoryOpenType
import com.yapp.weekand.domain.follow.repository.FollowRepository
import com.yapp.weekand.domain.schedule.entity.QScheduleRule.scheduleRule
import com.yapp.weekand.domain.schedule.entity.RepeatType
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.entity.Status
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleRepositorySupportImpl(
	private val queryFactory: JPAQueryFactory,
	private val followRepository: FollowRepository
) : ScheduleRepositorySupport, QuerydslRepositorySupport(ScheduleRule::class.java) {
	override fun getUserSchedulesByDate(dateYmd: String, userId: Long, currentUserId: Long): List<ScheduleRule> {
		val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
		val targetDate = LocalDate.parse(dateYmd, formatter)
		val targetDateDayOfWeek = convertToWeek(targetDate.dayOfWeek)

		val scheduleRules = queryFactory
			.selectFrom(scheduleRule)
			.where(
				scheduleRule.user.id.eq(userId),
				scheduleRule.dateStart.loe(targetDate.atTime(LocalTime.MAX)),
				scheduleRule.dateRepeatEnd.isNull.or(scheduleRule.dateRepeatEnd.goe(targetDate.atTime(LocalTime.MIN))),
				openType(userId, currentUserId)
			)
			.fetch()

		val noneWeeklyRules = scheduleRules
			.filter { it.repeatType != RepeatType.WEEKLY }
			.map { filterNoneWeeklyRules(it, targetDate) }
			.mapNotNull { it }
			.filter { rule -> filterSkipSchedule(rule) }

		val targetWeeklyRules = scheduleRules
			.filter { it.repeatType == RepeatType.WEEKLY }
			.filter { it.repeatSelectedValue.split(",").contains(targetDateDayOfWeek.toString()) }
			.filter { rule -> filterSkipSchedule(rule) }

		return (noneWeeklyRules + targetWeeklyRules).sortedBy { it.dateStart.toLocalTime() }
	}

	private fun openType(userId: Long, currentUserId: Long): BooleanExpression? {
		if (userId == currentUserId) {
			return null
		}
		if (isFollowed(userId, currentUserId)) {
			return scheduleRule.scheduleCategory.openType.`in`(ScheduleCategoryOpenType.FOLLOWER_OPEN, ScheduleCategoryOpenType.ALL_OPEN)
		}
		return scheduleRule.scheduleCategory.openType.eq(ScheduleCategoryOpenType.ALL_OPEN)
	}

	private fun isFollowed(userId: Long, currentUserId: Long) =
		followRepository.existsByFollowerUserIdAndFolloweeUserId(userId, currentUserId) && followRepository.existsByFollowerUserIdAndFolloweeUserId(currentUserId, userId)

	private fun filterSkipSchedule(scheduleRule: ScheduleRule): Boolean {
		val targetStatus = scheduleRule.scheduleStatus.find { it.dateYmd == scheduleRule.dateStart.toLocalDate() }
		if (targetStatus?.status == Status.SKIP) {
			return false
		}
		return true
	}

	private fun filterNoneWeeklyRules(scheduleRule: ScheduleRule, date: LocalDate): ScheduleRule? {
		var result: ScheduleRule? = null
		while (date >= scheduleRule.dateStart.toLocalDate()) {
			if (scheduleRule.dateStart.toLocalDate() == date && date == scheduleRule.dateEnd.toLocalDate()) {
				result = scheduleRule
				break
			}
			scheduleRule.dateStart = addDateByRepeatType(scheduleRule.dateStart, scheduleRule.repeatType)
			scheduleRule.dateEnd = addDateByRepeatType(scheduleRule.dateEnd, scheduleRule.repeatType)
		}
		return result
	}

	private fun addDateByRepeatType(date: LocalDateTime, repeatType: RepeatType) =
		when (repeatType) {
		RepeatType.DAILY -> date.plusDays(1)
		RepeatType.WEEKLY -> date.plusWeeks(1)
		RepeatType.MONTHLY -> date.plusMonths(1)
		RepeatType.YEARLY -> date.plusYears(1)
		RepeatType.ONCE -> date.plusYears(2000)
	}

	private fun convertToWeek(dayOfWeek: DayOfWeek): Week {
		val targetDateDayOfWeek = when (dayOfWeek) {
			DayOfWeek.MONDAY -> Week.MONDAY
			DayOfWeek.TUESDAY -> Week.TUESDAY
			DayOfWeek.WEDNESDAY -> Week.WEDNESDAY
			DayOfWeek.THURSDAY -> Week.THURSDAY
			DayOfWeek.FRIDAY -> Week.FRIDAY
			DayOfWeek.SATURDAY -> Week.SATURDAY
			DayOfWeek.SUNDAY -> Week.SUNDAY
		}
		return targetDateDayOfWeek
	}
}
