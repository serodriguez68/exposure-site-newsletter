package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories

import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Subscriber
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface SubscriberRepository: CrudRepository<Subscriber, UUID> {

}