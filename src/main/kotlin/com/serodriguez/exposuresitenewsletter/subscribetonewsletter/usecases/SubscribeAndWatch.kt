package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Subscriber
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SubscriberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SubscribeAndWatch(
    @Autowired val subscriberRepo: SubscriberRepository
    // @Autowired val suburbsRepo: SuburbsRepository,
    // @Autowired val watchesRepo: WatchesRepository
) {
    fun call(newSubscriptionData: NewSubscriptionData) {
        // 1. Validate data
            // 1.1 Subscriber does not exist
            // TODO: If email is already subscribed, what to do?
            // 1.2 All watched are for valid suburbs

        // 2. Create subscriber
        val savedSubscriber = subscriberRepo.save(Subscriber(email=newSubscriptionData.subscriberData.email))

        // 3. Create watches
        // val suburbsToWatch = suburbsRepo.findAllByIDnewSubscriptionData.suburbsToWatchData.map { it.id })
        // val savedWatches = watchesRepo.saveAll(
        //     suburbsToWatch.map { Watch(subsriber=savedSubscriber, suburb=it) }
        // )

        // 4. Send welcome email
        // 5. Send first exposure site email (may or may not be the same as the one above)
        // 6. Return a success
    }
}