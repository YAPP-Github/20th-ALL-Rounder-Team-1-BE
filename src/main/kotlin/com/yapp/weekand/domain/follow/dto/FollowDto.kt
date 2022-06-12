package com.yapp.weekand.domain.follow.dto

class FollowDto {
	class followerList(
		val pageInfo: PageInfo,
		val followers: List<Follows>
	)

	class Follows (
		val id: Long?,
		val nickname: String,
		val goal: String,
		val profileFilename: String?
	)

	class PageInfo(
		val hasNext: Boolean
	)
}
