package com.yapp.weekand.domain.follow.entity

import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.common.entity.BaseEntity
import javax.persistence.*

@Entity
class Follow (
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "follow_id")
        val id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "followee_user_id", nullable = false)
        var followeeUser: User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "follower_user_id", nullable = false)
        var followerUser: User
): BaseEntity()
