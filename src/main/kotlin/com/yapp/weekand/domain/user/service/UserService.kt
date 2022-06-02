package com.yapp.weekand.domain.user.service

import com.yapp.weekand.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService (
	private val userRepository: UserRepository
){
	fun checkDuplicateNickname(nickname: String) = userRepository.existsUserByNickname(nickname)
}
