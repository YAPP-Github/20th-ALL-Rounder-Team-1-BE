package com.yapp.weekand.domain.schedule.entity

import com.yapp.weekand.common.entity.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class ScheduleStatus (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "schedule_status_id")
        val id: Long? = null,

        var status: Status,

        var date_yml: LocalDateTime,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "schedule_rule_id")
        var scheduleRule: ScheduleRule
) : BaseEntity()