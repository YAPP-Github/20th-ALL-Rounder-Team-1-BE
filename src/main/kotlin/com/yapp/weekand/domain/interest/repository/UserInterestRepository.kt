package com.yapp.weekand.domain.interest.repository

import com.yapp.weekand.domain.interest.entity.UserInterest
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserInterestRepository : JpaRepository<UserInterest, Long> {
	fun deleteAllByUser(user: User)
}
