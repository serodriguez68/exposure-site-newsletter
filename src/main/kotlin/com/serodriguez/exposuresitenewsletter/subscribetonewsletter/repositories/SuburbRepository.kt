package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories;

import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Suburb
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface SuburbRepository : CrudRepository<Suburb, UUID> {
}