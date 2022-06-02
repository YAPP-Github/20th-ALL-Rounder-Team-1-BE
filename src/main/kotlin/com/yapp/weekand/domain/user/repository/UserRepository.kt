package com.yapp.weekand.domain.user.repository

import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository :JpaRepository<User, Long> {
	fun existsUserByNickname(nickname:String): Boolean
	fun findUserByEmail(email: String): User?
}
