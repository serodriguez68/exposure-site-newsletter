package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories;

import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Watch
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface WatchRepository : CrudRepository<Watch, UUID> {
}