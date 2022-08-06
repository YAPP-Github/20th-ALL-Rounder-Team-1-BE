package com.yapp.weekand.domain.sticker.entity

import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.common.entity.BaseEntity
import java.time.LocalDate
import javax.persistence.*

@Entity
class ScheduleSticker(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_sticker_id")
	val id: Long? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	var user: User,

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_rule_id")
	var scheduleRule: ScheduleRule,

	@Enumerated(EnumType.STRING)
	var name: ScheduleStickerName,

	@Column(name = "schedule_date", columnDefinition = "스티커를 붙인 일정의 특정 날짜")
	var scheduleDate: LocalDate,
) : BaseEntity()
