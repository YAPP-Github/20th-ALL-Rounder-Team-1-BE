package com.yapp.weekand.domain.follow.dto

class FollowDto {
	class getFollowsResponse(
		val pageInfo: PageInfo,
		val follows: List<Follows>
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
