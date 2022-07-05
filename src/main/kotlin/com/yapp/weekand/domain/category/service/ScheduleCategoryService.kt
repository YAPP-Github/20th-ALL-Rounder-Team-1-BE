package com.yapp.weekand.domain.category.service

import com.yapp.weekand.api.generated.types.ScheduleCategoryInput
import com.yapp.weekand.api.generated.types.ScheduleCategory as ScheduleCategoryGraphql
import com.yapp.weekand.api.generated.types.ScheduleCategorySort
import com.yapp.weekand.api.generated.types.ScheduleInfo
import com.yapp.weekand.domain.auth.exception.UnauthorizedAccessException
import com.yapp.weekand.domain.category.exception.ScheduleCategoryNotFoundException
import com.yapp.weekand.domain.category.mapper.toGraphql
import com.yapp.weekand.domain.category.repository.ScheduleCategoryRepository
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.category.entity.ScheduleCategory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ScheduleCategoryService(
	private val scheduleCategoryRepository: ScheduleCategoryRepository,
	private val scheduleRepository: ScheduleRepository
) {
	fun getScheduleCategories(user: User, sort: ScheduleCategorySort, page: Int, size: Int): Slice<ScheduleCategoryGraphql> {
		val scheduleCategorySort: Sort = getSort(sort)

		return scheduleCategoryRepository.findByUser(user, PageRequest.of(page, size, scheduleCategorySort))
			.map {
				ScheduleCategoryGraphql(
					id = it.id.toString(),
					name = it.name,
					color = it.color,
					openType = it.openType,
				)
			}
	}

	fun searchSchedules(sort: ScheduleCategorySort, page: Int, size: Int, searchQuery: String?, categoryId: Long): Slice<ScheduleInfo> {
		scheduleCategoryRepository.findByIdOrNull(categoryId)
			?: throw ScheduleCategoryNotFoundException()

		val findScheduleRules =
			if (searchQuery == null) {
				scheduleRepository.findScheduleRules(PageRequest.of(page, size, getSort(sort)), categoryId)
			}
			else {
				scheduleRepository.searchScheduleRules(PageRequest.of(page, size, getSort(sort)), searchQuery, categoryId)
			}

		return findScheduleRules.map {
				ScheduleInfo(
					id = it.id.toString(),
					name = it.name,
					category = it.scheduleCategory.toGraphql(),
					dateTimeStart = it.dateStart,
					dateTimeEnd = it.dateEnd,
					repeatType = it.repeatType,
					repeatSelectedValue = it.repeatSelectedValue,
					memo = it.memo
				)
			}
	}

	@Transactional
	fun createCategory(categoryInput: ScheduleCategoryInput, user: User): Boolean {
		scheduleCategoryRepository.save(ScheduleCategory.of(categoryInput, user))
		return true
	}

	@Transactional
	fun updateCategory(categoryId: Long, categoryInput: ScheduleCategoryInput, user: User): Boolean {
		val category = scheduleCategoryRepository.findByIdOrNull(categoryId)
			?: throw ScheduleCategoryNotFoundException()

		if (category.user != user) {
			throw UnauthorizedAccessException()
		}

		category.updateCategory(categoryInput)
		return true
	}

	private fun getSort(sort: ScheduleCategorySort): Sort {
		val scheduleCategorySort: Sort = when (sort) {
			ScheduleCategorySort.DATE_CREATED_ASC -> Sort.by("dateCreated").ascending()
			ScheduleCategorySort.DATE_CREATED_DESC -> Sort.by("dateCreated").descending()
			ScheduleCategorySort.NAME_ASC -> Sort.by("name").ascending()
			ScheduleCategorySort.NAME_DESC -> Sort.by("name").descending()
		}
		return scheduleCategorySort
	}
}
