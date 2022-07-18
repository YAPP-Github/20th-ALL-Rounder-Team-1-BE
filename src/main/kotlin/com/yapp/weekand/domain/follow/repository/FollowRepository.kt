package com.yapp.weekand.domain.follow.repository

import com.yapp.weekand.domain.follow.entity.Follow
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository: JpaRepository<Follow, Long> {
	fun findByFolloweeUserOrderByDateCreatedDesc(user: User, pageable: Pageable): Slice<Follow>

	fun findByFollowerUserOrderByDateCreatedDesc(user: User, pageable: Pageable): Slice<Follow>

	fun findByFollowerUserAndFolloweeUser(followerUser: User, followeeUser: User): Slice<Follow>

	fun findByFollowerUserOrFolloweeUser(followerUser: User, followeeUser: User): List<Follow>
}
