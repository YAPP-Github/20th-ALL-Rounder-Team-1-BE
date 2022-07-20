package com.yapp.weekand.domain.user.entity

import com.yapp.weekand.domain.interest.entity.UserInterest
import com.yapp.weekand.domain.follow.entity.Follow
import com.yapp.weekand.domain.job.entity.UserJob
import com.yapp.weekand.domain.notification.entity.Notification
import com.yapp.weekand.domain.schedule.entity.ScheduleRule
import com.yapp.weekand.common.entity.BaseEntity
import com.yapp.weekand.domain.category.entity.ScheduleCategory
import javax.persistence.*

@Entity
class User (
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long = 0L,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var nickname: String,

    @Column(nullable = false)
    var password: String,

    var goal: String = "",

    var followerCount: Int = 0,

    var profileFilename: String? = null,

    @OneToMany(mappedBy = "user")
    var interests: MutableList<UserInterest> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    var jobs: MutableList<UserJob> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    var scheduleRules: MutableList<ScheduleRule> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    var notifications: MutableList<Notification> = mutableListOf(),

	@OneToMany(mappedBy = "user")
	var schedulecategories: MutableList<ScheduleCategory> = mutableListOf(),

    @OneToMany(mappedBy = "followerUser")
    var followerList: MutableList<Follow> = mutableListOf(),

    @OneToMany(mappedBy = "followeeUser")
    var followeeList: MutableList<Follow> = mutableListOf()
) : BaseEntity() {
	fun updatePassword(password: String) {
		this.password = password
	}
}
