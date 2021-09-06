package com.serodriguez.exposuresitenewsletter.base

import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class AuditedEntity {
    @CreatedDate
    @Column(name="created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime
}

@Configuration
@EnableJpaAuditing
class AuditingConfiguration {
    // Intentionally left blank. This annotated class is needed to enable Spring Data JPA auditing
}