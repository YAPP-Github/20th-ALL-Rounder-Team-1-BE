package com.yapp.weekand.domain.notification.entity

import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.common.entity.BaseEntity
import javax.persistence.*

@Entity
class Notification(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "notification_id")
        val id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        var user: User,

        var message: String,

        @Enumerated(EnumType.STRING)
        var type: NotificationType
) : BaseEntity()
