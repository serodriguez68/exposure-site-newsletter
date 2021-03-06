package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities

import com.serodriguez.exposuresitenewsletter.base.AuditedEntity
import org.hibernate.annotations.GenericGenerator
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Table(name = "subscribers")
@Entity
class Subscriber(
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    val id: UUID? = null,

    @Column(name = "email", nullable = false)
    val email: String,

    @OneToMany(mappedBy = "subscriber", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val watches: List<Watch> = listOf(),

): AuditedEntity()