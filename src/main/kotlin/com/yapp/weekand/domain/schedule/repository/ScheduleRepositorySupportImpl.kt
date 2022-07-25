package com.yapp.weekand.domain.schedule.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.weekand.api.generated.types.Week
import com.yapp.weekand.domain.schedule.entity.QScheduleRule
import com.yapp.weekand.domain.schedule.entity.RepeatType
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.schedule.entity.Status
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleRepositorySupportImpl(
	private val queryFactory: JPAQueryFactory,
) : ScheduleRepositorySupport, QuerydslRepositorySupport(ScheduleRule::class.java) {
	override fun getUserSchedulesByDate(dateYmd: String, userId: Long): List<ScheduleRule> {
		val qScheduleRule = QScheduleRule.scheduleRule
		val query = queryFactory.selectFrom(qScheduleRule)

		val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
		val targetDate = LocalDateTime.parse(dateYmd, formatter)

		val targetDateDayOfWeek = when (targetDate.dayOfWeek) {
			DayOfWeek.MONDAY -> Week.MONDAY
			DayOfWeek.TUESDAY -> Week.TUESDAY
			DayOfWeek.WEDNESDAY -> Week.WEDNESDAY
			DayOfWeek.THURSDAY -> Week.THURSDAY
			DayOfWeek.FRIDAY -> Week.FRIDAY
			DayOfWeek.SATURDAY -> Week.SATURDAY
			DayOfWeek.SUNDAY -> Week.SUNDAY
		}

		val scheduleRules = query
			.where(qScheduleRule.user.id.eq(userId))
			.where(qScheduleRule.dateStart.loe(targetDate))
			.where(
				qScheduleRule.dateRepeatEnd.isNull.or(qScheduleRule.dateRepeatEnd.goe(targetDate))
			)
			.fetch()

		val NoneWeeklyRules = scheduleRules
			.filter { it.repeatType != RepeatType.WEEKLY }
			.map { filterNoneWeeklyRules(it, targetDate.toLocalDate()) }
			.mapNotNull { it }
			.filter { rule -> filterSkipSchedule(rule) }

		val TargetWeeklyRules = scheduleRules
			.filter { it.repeatType == RepeatType.WEEKLY }
			.filter { it.repeatSelectedValue != null }
			.filter { it.repeatSelectedValue!!.split(",").contains(targetDateDayOfWeek.toString()) }
			.filter { rule -> filterSkipSchedule(rule) }

		return NoneWeeklyRules + TargetWeeklyRules
	}

	fun filterSkipSchedule(scheduleRule: ScheduleRule): Boolean {
		val targetStatus = scheduleRule.scheduleStatus.find { it.dateYmd == scheduleRule.dateStart.toLocalDate() }
		if (targetStatus?.status == Status.SKIP) {
			return false
		}
		return true
	}

	fun filterNoneWeeklyRules(scheduleRule: ScheduleRule, date: LocalDate): ScheduleRule? {
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

	private fun addDateByRepeatType(date: LocalDateTime, repeatType: RepeatType) = when (repeatType) {
		RepeatType.DAILY -> date.plusDays(1)
		RepeatType.WEEKLY -> date.plusWeeks(1)
		RepeatType.MONTHLY -> date.plusMonths(1)
		RepeatType.YEARLY -> date.plusYears(1)
		RepeatType.ONCE -> date.plusYears(2000)
	}

}
