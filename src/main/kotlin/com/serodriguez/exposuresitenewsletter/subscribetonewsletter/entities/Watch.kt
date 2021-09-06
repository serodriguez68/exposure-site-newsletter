package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities

import com.serodriguez.exposuresitenewsletter.base.AuditedEntity
import org.hibernate.annotations.GenericGenerator
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Table(name = "watches")
@Entity
class Watch(
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name="subscriber_id")
    val subscriber: Subscriber,

    @ManyToOne
    @JoinColumn(name="suburb_id")
    val suburb: Suburb
): AuditedEntity()