package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities

import org.hibernate.annotations.GenericGenerator
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "suburbs")
@Entity
class Suburb(
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    val id: UUID? = null,

    @Column(name="post_code", nullable = false)
    val postCode: String,

    @Column(name="name", nullable = false)
    val name: String
)