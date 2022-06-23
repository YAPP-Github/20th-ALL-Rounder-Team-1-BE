package com.yapp.weekand.domain.job.entity

import com.yapp.weekand.domain.user.entity.User
import javax.persistence.*

@Entity
class UserJob(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_job_id")
        val id: Long? = null,

        var jobName: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        var user: User
)
