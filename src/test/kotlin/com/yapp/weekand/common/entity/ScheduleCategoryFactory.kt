package com.yapp.weekand.common.entity

import com.yapp.weekand.api.generated.types.ScheduleCategoryInput
import com.yapp.weekand.domain.category.entity.ScheduleCategoryOpenType

class ScheduleCategoryFactory {
	companion object {
		fun categoryInput () =
			ScheduleCategoryInput (
				name = "공부",
				color = "blue",
				openType = ScheduleCategoryOpenType.FOLLOWER_OPEN
			)
	}
}
