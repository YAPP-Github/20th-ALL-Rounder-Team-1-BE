package com.yapp.weekand.domain.category.service

import com.yapp.weekand.api.generated.types.ScheduleCategoryInput
import com.yapp.weekand.api.generated.types.ScheduleCategory as ScheduleCategoryGraphql
import com.yapp.weekand.api.generated.types.ScheduleCategorySort
import com.yapp.weekand.api.generated.types.ScheduleRule
import com.yapp.weekand.domain.auth.exception.UnauthorizedAccessException
import com.yapp.weekand.domain.category.exception.ScheduleCategoryNotFoundException
import com.yapp.weekand.domain.category.repository.ScheduleCategoryRepository
import com.yapp.weekand.domain.schedule.repository.ScheduleRepository
import com.yapp.weekand.domain.user.entity.User
import com.yapp.weekand.domain.category.entity.ScheduleCategory
import com.yapp.weekand.domain.category.exception.ScheduleCategoryDuplicatedNameException
import com.yapp.weekand.domain.category.exception.ScheduleCategoryUnderMinSizeException
import com.yapp.weekand.domain.schedule.mapper.toScheduleRuleGraphql
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
	fun findScheduleCategoryById(id: Long) = scheduleCategoryRepository.findById(id).get()
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

	fun searchSchedules(sort: ScheduleCategorySort, page: Int, size: Int, searchQuery: String?, categoryId: Long): Slice<ScheduleRule> {
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
				it.toScheduleRuleGraphql()
			}
	}

	@Transactional
	fun createCategory(categoryInput: ScheduleCategoryInput, user: User): Boolean {
		if (scheduleCategoryRepository.existsByName(categoryInput.name)) {
			throw ScheduleCategoryDuplicatedNameException()
		}

		scheduleCategoryRepository.save(
			ScheduleCategory.of(categoryInput, user)
		)
		return true
	}

	@Transactional
	fun createDefaultCategory(user: User) {
		scheduleCategoryRepository.save(
			ScheduleCategory.of(user)
		)
	}

	@Transactional
	fun updateCategory(categoryId: Long, categoryInput: ScheduleCategoryInput, user: User): Boolean {
		val category = scheduleCategoryRepository.findByIdOrNull(categoryId)
			?: throw ScheduleCategoryNotFoundException()

		if (categoryInput.name != category.name && scheduleCategoryRepository.existsByName(categoryInput.name)) {
			throw ScheduleCategoryDuplicatedNameException()
		}

		if (category.user.id != user.id) {
			throw UnauthorizedAccessException()
		}

		category.updateCategory(categoryInput)
		return true
	}

	@Transactional
	fun deleteCategory(categoryId: Long, user: User) {
		if (user.schedulecategories.size < MIN_CATEGORY_SIZE) {
			throw ScheduleCategoryUnderMinSizeException()
		}

		val category = scheduleCategoryRepository.findByIdOrNull(categoryId)
			?: throw ScheduleCategoryNotFoundException()

		if (user.id != category.user.id) {
			throw UnauthorizedAccessException()
		}

		val schedules = scheduleRepository.findByScheduleCategory(category)
		scheduleRepository.deleteAllInBatch(schedules)
		scheduleCategoryRepository.delete(category)
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

	@Transactional
	fun deleteCategoryByUser(user: User) {
		val scheduleCategories = scheduleCategoryRepository.findByUser(user)
		scheduleCategoryRepository.deleteAllInBatch(scheduleCategories)
	}

	companion object {
		private const val MIN_CATEGORY_SIZE = 2
	}
}
