package com.yapp.weekand.domain.user.repository

import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.user.service.UserService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
internal class UserRepositoryTest {
	@Autowired
	lateinit var userRepository: UserRepository

	@Autowired
	lateinit var userService: UserService

	@Test
	@DisplayName("닉네임 중복 확인을 한다.")
	fun checkDuplicateNickname() {
		// given
		var user = User(email= "test@test.com",
			nickname = "test",
			password = "1234!",
			marketingAgreed = true)

		// when
		userRepository.save(user)
		val checkDuplicateNickname = userService.checkDuplicateNickname("test")

		// then
		Assertions.assertThat(checkDuplicateNickname).isEqualTo(true)
	}

	@Test
	@DisplayName("회원 ID를 통해 회원을 조회한다")
	fun findUserById() {
		// given
		var user = User(
			email= "test@test.com",
			nickname = "test",
			password = "1234!",
			marketingAgreed = true)

		// when
		val savedUser = userRepository.save(user)
		val foundUser = userService.findUserById(savedUser.id)

		// then
		Assertions.assertThat(foundUser).isEqualTo(user)
	}
}
