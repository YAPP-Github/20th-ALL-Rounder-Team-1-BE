package com.yapp.weekand.domain.user.service

import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.auth.exception.UserNotFoundException
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService (
	private val userRepository: UserRepository,
	private val jwtProvider: JwtProvider
){
	fun checkDuplicateNickname(nickname: String) = userRepository.existsUserByNickname(nickname)

	fun findUserById(id: Long) = userRepository.findById(id).get()

	fun getCurrentUser(): User {
		val currentUserId = jwtProvider.getFromSecurityContextHolder().user.id
		return userRepository.findByIdOrNull(currentUserId)
			?: throw UserNotFoundException()
	}
}
