package com.yapp.weekand.domain.user.service

import com.yapp.weekand.common.jwt.JwtProvider
import com.yapp.weekand.domain.auth.exception.UserNotFoundException
import com.yapp.weekand.domain.interest.entity.UserInterest
import com.yapp.weekand.domain.interest.repository.UserInterestRepository
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
@Transactional(readOnly = true)
class UserService (
	private val userRepository: UserRepository,
	private val jwtProvider: JwtProvider,
	private val userInterestRepository: UserInterestRepository
){
	fun checkDuplicateNickname(nickname: String) = userRepository.existsUserByNickname(nickname)

	fun findUserById(id: Long) = userRepository.findById(id).get()

	fun getCurrentUser(): User {
		val currentUserId = jwtProvider.getFromSecurityContextHolder().user.id
		return userRepository.findByIdOrNull(currentUserId)
			?: throw UserNotFoundException()
	}

	@Transactional
	fun updateInterests(user: User, interests: List<String>): String {
		if (userInterestRepository.existsByUser(user)) {
			val oldInterests = userInterestRepository.findByUser(user)
			userInterestRepository.deleteAll(oldInterests)
		}

		val userInterests: MutableList<UserInterest> = interests.stream()
			.map { i -> UserInterest.of(user, i) }
			.collect(Collectors.toList())
		userInterestRepository.saveAll(userInterests)

		user.updateInterest(userInterests)
		return "succeed"
	}
}
