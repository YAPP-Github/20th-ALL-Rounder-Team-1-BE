package com.yapp.weekand.domain.user.mapper

import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.api.generated.types.User as UserGraphql

fun User.toGraphql() =
	UserGraphql(
		id = id.toString(),
		email = email,
		nickname = nickname,
		profileUrl = profileImageFilename.orEmpty(),
		goal = goal,
		followerCount = followerCount,
		followeeCount = followerCount,
		jobs = jobs.map { it.jobName },
		interests = interests.map { it.interestName },
		followed = false
	)
