package com.yapp.weekand.domain.interest.repository

import com.yapp.weekand.domain.interest.entity.UserInterest
import org.springframework.data.jpa.repository.JpaRepository

interface UserInterestRepository: JpaRepository<UserInterest, Long>
