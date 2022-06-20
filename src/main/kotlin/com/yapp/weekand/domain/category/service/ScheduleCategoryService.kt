package com.yapp.weekand.domain.category.service

import com.yapp.weekand.api.generated.types.ScheduleCategory
import com.yapp.weekand.api.generated.types.ScheduleCategorySort
import com.yapp.weekand.domain.category.repository.ScheduleCategoryRepository
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ScheduleCategoryService(
	private val scheduleCategoryRepository: ScheduleCategoryRepository
) {
	fun getScheduleCategories(user: User, sort: ScheduleCategorySort, page: Int, size: Int): Slice<ScheduleCategory> {
		val scheduleCategorySort: Sort = when (sort) {
			ScheduleCategorySort.DATE_CREATED_ASC -> Sort.by("dateCreated").ascending()
			ScheduleCategorySort.DATE_CREATED_DESC -> Sort.by("dateCreated").descending()
			ScheduleCategorySort.NAME_ASC -> Sort.by("name").ascending()
			ScheduleCategorySort.NAME_DESC -> Sort.by("name").descending()
		}

		return scheduleCategoryRepository.findByUser(user, PageRequest.of(page, size, scheduleCategorySort))
			.map {
				ScheduleCategory(
					id = it.id.toString(),
					name = it.name,
					color = it.color,
					openType = it.openType,
				)
			}
	}
}
