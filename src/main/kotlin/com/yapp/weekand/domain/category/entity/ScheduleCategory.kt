package com.yapp.weekand.domain.category.entity

import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.common.entity.BaseEntity
import javax.persistence.*

@Entity
class ScheduleCategory (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "schedule_category_id")
        var id: Long? = null,

        var name: String,

        var color: String,

        @Enumerated(EnumType.STRING)
        var openType: OpenType,

        @OneToMany(mappedBy = "scheduleCategory")
        var scheduleRules: MutableList<ScheduleRule> = mutableListOf()
): BaseEntity()