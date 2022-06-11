package com.yapp.weekand.domain.follow.repository

import com.yapp.weekand.domain.follow.entity.Follow
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository: JpaRepository<Follow, Long> {
	fun findByFolloweeUserOrderByDateCreated(user: User, pageable: Pageable): Slice<Follow>
}
