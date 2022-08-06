package com.yapp.weekand.common.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(value = [AuditingEntityListener::class])
abstract class BaseEntity {
    @CreatedDate
    @Column(name = "date_created", updatable = false)
    var dateCreated: LocalDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime()

    @LastModifiedDate
	@Column(name = "date_updated")
	var dateUpdated: LocalDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime()
}
