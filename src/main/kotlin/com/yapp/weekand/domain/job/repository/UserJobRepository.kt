package com.yapp.weekand.domain.job.repository

import com.yapp.weekand.domain.job.entity.UserJob
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserJobRepository: JpaRepository<UserJob, Long> {
	fun deleteAllByUser(user: User)

	fun findByUser(user: User): List<UserJob>
}
