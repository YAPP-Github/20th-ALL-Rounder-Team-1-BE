package com.yapp.weekand.domain.job.service

import com.yapp.weekand.domain.job.entity.UserJob
import com.yapp.weekand.domain.job.repository.UserJobRepository
import com.yapp.weekand.domain.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class JobService(
	private val jobRepository: UserJobRepository,
) {
	@Transactional
	fun deleteAllUserJob(user: User) = jobRepository.deleteAllByUser(user)

	@Transactional
	fun createUserJobList(user: User, jobs: List<String>) {
		for (job in jobs) {
			jobRepository.save(
				UserJob(
					user = user,
					jobName = job
				)
			)
		}
	}
}
