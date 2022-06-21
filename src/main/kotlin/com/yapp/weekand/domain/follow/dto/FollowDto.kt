package com.yapp.weekand.domain.follow.dto

import com.yapp.weekand.api.generated.types.PaginationInfo

class FollowDto {
	class FollowerList(
		val paginationInfo: PaginationInfo,
		val followers: List<Follows>
	)

	class FolloweeList(
		val paginationInfo: PaginationInfo,
		val followees: List<Follows>
	)

	class Follows (
		val id: Long?,
		val nickname: String,
		val goal: String?,
		val profileFilename: String?
	)

	class PaginationInfo(
		val hasNext: Boolean
	)
}
