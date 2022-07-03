package com.yapp.weekand.domain.job.repository

import com.yapp.weekand.domain.job.entity.UserJob
import org.springframework.data.jpa.repository.JpaRepository

interface UserJobRepository: JpaRepository<UserJob, Long>
