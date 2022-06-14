package com.yapp.weekand.domain.follow.dto

class FollowDto {
	class FollowerList(
		val pageInfo: PageInfo,
		val followers: List<Follows>
	)

	class FolloweeList(
		val pageInfo: PageInfo,
		val followees: List<Follows>
	)

	class Follows (
		val id: Long?,
		val nickname: String,
		val goal: String?,
		val profileFilename: String?
	)

	class PageInfo(
		val hasNext: Boolean
	)
}
