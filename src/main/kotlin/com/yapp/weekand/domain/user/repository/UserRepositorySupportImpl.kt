package com.yapp.weekand.domain.user.repository

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.weekand.api.generated.types.SearchUserSort
import com.yapp.weekand.domain.interest.entity.QUserInterest
import com.yapp.weekand.domain.job.entity.QUserJob
import com.yapp.weekand.domain.user.dto.SearchUserListCondition
import com.yapp.weekand.domain.user.entity.QUser
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class UserRepositorySupportImpl(
	private val queryFactory: JPAQueryFactory,
) : UserRepositorySupport, QuerydslRepositorySupport(User::class.java) {

	private fun buildSearchUserListQuery(condition: SearchUserListCondition): JPAQuery<User> {
		val qUser = QUser.user
		val query = queryFactory.selectFrom(qUser)

		val nickNameOrGoalQueryTrim = condition.nickNameOrGoalQuery?.trim()
		if (nickNameOrGoalQueryTrim != null && nickNameOrGoalQueryTrim.isNotEmpty()) {
			query.where(
				qUser.nickname.like("%${nickNameOrGoalQueryTrim}%").or(qUser.goal.like("%${nickNameOrGoalQueryTrim}%"))
			)
		}

		if (!condition.jobs.isNullOrEmpty()) {
			val qJob = QUserJob.userJob
			query.join(qUser.jobs, qJob).where(qJob.jobName.`in`(condition.jobs))
		}

		if (!condition.interests.isNullOrEmpty()) {
			val qInterest = QUserInterest.userInterest
			query.join(qUser.interests, qInterest).where(qInterest.interestName.`in`(condition.interests))
		}

		when (condition.sort) {
			SearchUserSort.DATE_CREATED_DESC -> query.orderBy(qUser.dateCreated.desc())
			SearchUserSort.NICKNAME_ASC -> query.orderBy(qUser.nickname.asc())
			SearchUserSort.NICKNAME_DESC -> query.orderBy(qUser.nickname.desc())
			SearchUserSort.FOLLOWER_COUNT_DESC -> query.orderBy(qUser.followerCount.desc())
			else -> query.orderBy(qUser.dateCreated.desc())
		}

		return query
	}

	override fun searchUserListWithPaging(
		condition: SearchUserListCondition,
		pageable: Pageable,
	): Page<User> {
		val content = buildSearchUserListQuery(condition)
			.offset(pageable.offset)
			.limit(pageable.pageSize.toLong())
			.fetch()

		val totalCount = buildSearchUserListQuery(condition)
			.fetch()
			.size

		return PageImpl(content, pageable, totalCount.toLong())
	}
}
