package com.yapp.weekand.domain.category.entity

import com.yapp.weekand.api.generated.types.ScheduleCategoryInput
import com.yapp.weekand.common.entity.BaseEntity
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.user.entity.User
import javax.persistence.*

@Entity
class ScheduleCategory(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_category_id")
	var id: Long? = null,

	var name: String,

	var color: String,

	@Enumerated(EnumType.STRING)
	var openType: ScheduleCategoryOpenType,

	@OneToMany(mappedBy = "scheduleCategory")
	var scheduleRules: MutableList<ScheduleRule> = mutableListOf(),

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	var user: User,
) : BaseEntity() {
	fun updateCategory(scheduleCategoryInput: ScheduleCategoryInput) {
		this.name = scheduleCategoryInput.name
		this.color = scheduleCategoryInput.color
		this.openType = scheduleCategoryInput.openType
	}

	companion object {
		fun of(categoryInput: ScheduleCategoryInput, user: User): ScheduleCategory {
			return ScheduleCategory(
				name = categoryInput.name,
				color = categoryInput.color,
				openType = categoryInput.openType,
				user = user
			)
		}

		fun of(user: User): ScheduleCategory {
			return ScheduleCategory(
				name = "내 일정",
				color = "#ff9292",
				openType = ScheduleCategoryOpenType.CLOSED,
				user = user
			)
		}
	}
}
