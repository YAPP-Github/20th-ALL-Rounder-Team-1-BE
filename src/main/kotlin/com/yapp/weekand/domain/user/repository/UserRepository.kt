package com.yapp.weekand.domain.user.repository

import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository :JpaRepository<User, Long>, UserRepositorySupport {
	fun existsUserByNickname(nickname:String): Boolean
	fun findByEmail(email: String): User?
	fun existsUserByEmail(email: String): Boolean
}
