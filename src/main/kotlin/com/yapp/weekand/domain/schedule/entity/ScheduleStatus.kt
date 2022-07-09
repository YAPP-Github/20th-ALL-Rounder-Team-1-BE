package com.yapp.weekand.domain.schedule.entity

import com.yapp.weekand.common.entity.BaseEntity
import java.time.LocalDate
import javax.persistence.*

@Entity
class ScheduleStatus (
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_status_id")
	val id: Long? = null,

	@Enumerated(EnumType.STRING)
	var status: Status,

	var dateYmd: LocalDate,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_rule_id")
	var scheduleRule: ScheduleRule
) : BaseEntity() {
	companion object {
		fun skipSchedule(skipDate: LocalDate, scheduleRule: ScheduleRule): ScheduleStatus{
			return ScheduleStatus(
				status = Status.SKIP,
				dateYmd = skipDate,
				scheduleRule = scheduleRule
			)
		}
	}
}
