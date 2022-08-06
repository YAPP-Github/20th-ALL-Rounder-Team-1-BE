package com.yapp.weekand.domain.user.repository

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.weekand.api.generated.types.SearchUserSort
import com.yapp.weekand.domain.interest.entity.QUserInterest
import com.yapp.weekand.domain.job.entity.QUserJob
import com.yapp.weekand.domain.user.dto.SearchUserListCondition
import com.yapp.weekand.domain.user.entity.QUser.user
import com.yapp.weekand.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class UserRepositorySupportImpl(
	private val queryFactory: JPAQueryFactory,
) : UserRepositorySupport, QuerydslRepositorySupport(User::class.java) {

	private fun buildSearchUserListQuery(condition: SearchUserListCondition): JPAQuery<User> {
		val query = queryFactory.selectFrom(user)

		val nickNameOrGoalQueryTrim = condition.nickNameOrGoalQuery?.trim()
		if (nickNameOrGoalQueryTrim != null && nickNameOrGoalQueryTrim.isNotEmpty()) {
			query.where(
				user.nickname.like("%${nickNameOrGoalQueryTrim}%").or(user.goal.like("%${nickNameOrGoalQueryTrim}%"))
			)
		}

		if (!condition.jobs.isNullOrEmpty()) {
			val qJob = QUserJob.userJob
			query.join(user.jobs, qJob).where(qJob.jobName.`in`(condition.jobs))
		}

		if (!condition.interests.isNullOrEmpty()) {
			val qInterest = QUserInterest.userInterest
			query.join(user.interests, qInterest).where(qInterest.interestName.`in`(condition.interests))
		}

		when (condition.sort) {
			SearchUserSort.DATE_CREATED_DESC -> query.orderBy(user.dateCreated.desc())
			SearchUserSort.NICKNAME_ASC -> query.orderBy(user.nickname.asc())
			SearchUserSort.NICKNAME_DESC -> query.orderBy(user.nickname.desc())
			SearchUserSort.FOLLOWER_COUNT_DESC -> query.orderBy(user.followerCount.desc())
			else -> query.orderBy(user.dateCreated.desc())
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

	override fun minusFollowerCount(targetUserIds: List<Long>) {
		queryFactory
			.update(user)
			.set(user.followerCount, user.followerCount.add(-1))
			.where(user.id.`in`(targetUserIds))
			.execute()
	}
}
