package com.yapp.weekand.domain.interest.entity

import com.yapp.weekand.domain.user.entity.User
import javax.persistence.*

@Entity
class UserInterest(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_interest_id")
        val id: Long? = null,

        @Enumerated(EnumType.STRING)
        var interestName: Interest,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        var user: User
)