package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import arrow.core.Either
import arrow.core.computations.either
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Subscriber
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SubscriberRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.validations.NewSubscriptionDataValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SubscribeAndWatch(
    @Autowired val subscriberRepo: SubscriberRepository,
    @Autowired val newSubscriptionDataValidator: NewSubscriptionDataValidator
    // @Autowired val suburbsRepo: SuburbsRepository,
    // @Autowired val watchesRepo: WatchesRepository
) {
    suspend fun call(newSubscriptionData: NewSubscriptionData): Either<SubscribeAndWatchError, Subscriber> = either {
        // 1. Validate data
        val validatedNewSubscriptionData =
            newSubscriptionDataValidator.validate(newSubscriptionData, subscriberRepo::existsByEmail)
                .toEither()
                .bind()

        // 2. Create subscriber
        val savedSubscriber = subscriberRepo.save(
            Subscriber(email = validatedNewSubscriptionData.subscriberData.email)
        )

        // 3. Create watches
        // val suburbsToWatch = suburbsRepo.findAllByIDnewSubscriptionData.suburbsToWatchData.map { it.id })
        // val savedWatches = watchesRepo.saveAll(
        //     suburbsToWatch.map { Watch(subscriber=savedSubscriber, suburb=it) }
        // )

        // 4. Send welcome email
        // 5. Send first exposure site email (may or may not be the same as the one above)
        // 6. Return a success
        savedSubscriber
    }
}