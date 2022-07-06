package com.yapp.weekand.common.entity

import com.yapp.weekand.api.generated.types.ScheduleCategoryInput
import com.yapp.weekand.domain.category.entity.ScheduleCategory
import com.yapp.weekand.domain.category.entity.ScheduleCategoryOpenType
import com.yapp.weekand.domain.user.entity.User

class ScheduleCategoryFactory {
	companion object {
		fun categoryInput () =
			ScheduleCategoryInput (
				name = "공부",
				color = "blue",
				openType = ScheduleCategoryOpenType.FOLLOWER_OPEN
			)

		fun categoryUpdateInput () =
			ScheduleCategoryInput (
				name = "공하싫",
				color = "pink",
				openType = ScheduleCategoryOpenType.FOLLOWER_OPEN
			)

		fun category (user: User) =
			ScheduleCategory (
				id = 1L,
				name = "스터디",
				color = "white",
				openType = ScheduleCategoryOpenType.ALL_OPEN,
				user = user
			)
	}
}
