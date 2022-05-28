package com.yapp.weekand.domain.schedule.entity

import javax.persistence.*

@Entity
class RepeatWeek (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "repeat_week_id")
        val id: Long? = null,

        @Enumerated(EnumType.STRING)
        var week: Week,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "schedule_rule_id")
        var scheduleRule: ScheduleRule
)