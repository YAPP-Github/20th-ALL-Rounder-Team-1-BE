package com.yapp.weekand.common.jwt.user

import com.yapp.weekand.domain.auth.exception.UserNotFoundException
import com.yapp.weekand.domain.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailServiceImpl(
	private val userRepository: UserRepository
): UserDetailsService {
	override fun loadUserByUsername(username: String): UserDetails {
		return UserDetailsImpl(userRepository.findByEmail(username) ?: throw UserNotFoundException())
	}
}
