package com.yapp.weekand.domain.interest.service

import com.yapp.weekand.domain.interest.entity.UserInterest
import com.yapp.weekand.domain.interest.repository.UserInterestRepository
import com.yapp.weekand.domain.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class InterestService(
	private val interestRepository: UserInterestRepository,
) {
	@Transactional
	fun deleteAllUserInterest(user: User) = interestRepository.deleteAllByUser(user)

	@Transactional
	fun createUserInterestList(user: User, interests: List<String>) {
		for (interest in interests) {
			interestRepository.save(
				UserInterest(
					user = user,
					interestName = interest
				)
			)
		}
	}

	@Transactional
	fun deleteInterestByUser(user: User) {
		val interests = interestRepository.findByUser(user)
		interestRepository.deleteAllInBatch(interests)
	}
}
