package com.yapp.weekand.domain.schedule.entity

import com.yapp.weekand.api.generated.types.ScheduleInput
import com.yapp.weekand.api.generated.types.UpdateScheduleInput
import com.yapp.weekand.domain.category.entity.ScheduleCategory
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.common.entity.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class ScheduleRule (
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_rule_id")
	val id: Long? = null,

	var name: String,

	var dateStart: LocalDateTime,

	var dateEnd: LocalDateTime,

	var dateRepeatEnd: LocalDateTime? = null,

	@Column(length = 500)
	var memo: String?,

	@Enumerated(EnumType.STRING)
	var repeatType: RepeatType,

	var repeatSelectedValue: String,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	var user: User,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_category_id")
	var scheduleCategory: ScheduleCategory,

	@OneToMany(mappedBy = "scheduleRule")
	var scheduleStatus: MutableList<ScheduleStatus> = mutableListOf()
) : BaseEntity() {
	fun updateDateRepeatEnd(dateRepeatEnd: LocalDateTime) {
		this.dateRepeatEnd = dateRepeatEnd
	}

	companion object {
		fun of (scheduleInput: ScheduleInput, scheduleCategory: ScheduleCategory, user: User): ScheduleRule {
			return ScheduleRule(
				name = scheduleInput.name,
				dateStart = scheduleInput.dateTimeStart,
				dateEnd = scheduleInput.dateTimeEnd,
				dateRepeatEnd = scheduleInput.repeatEnd,
				repeatType = scheduleInput.repeatType,
				memo = scheduleInput.memo,
				repeatSelectedValue = scheduleInput.repeatSelectedValue.joinToString(","),
				scheduleCategory = scheduleCategory,
				user = user
			)
		}

		fun of (scheduleInput: UpdateScheduleInput, scheduleCategory: ScheduleCategory, user: User): ScheduleRule {
			return ScheduleRule(
				id = scheduleInput.id.toLong(),
				name = scheduleInput.name,
				dateStart = scheduleInput.dateTimeStart,
				dateEnd = scheduleInput.dateTimeEnd,
				dateRepeatEnd = scheduleInput.repeatEnd,
				repeatType = scheduleInput.repeatType,
				memo = scheduleInput.memo,
				repeatSelectedValue = scheduleInput.repeatSelectedValue.joinToString(","),
				scheduleCategory = scheduleCategory,
				user = user
			)
		}
	}
}
