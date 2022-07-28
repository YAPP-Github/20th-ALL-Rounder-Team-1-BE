package com.yapp.weekand.domain.follow.mapper

import com.yapp.weekand.api.generated.types.FollowUser
import com.yapp.weekand.domain.user.entity.User

fun User.toFollowUserGraphql() =
	FollowUser(
		id = id.toString(),
		nickname = nickname,
		goal = goal,
		profileImageUrl = profileImageFilename.orEmpty()
	)
