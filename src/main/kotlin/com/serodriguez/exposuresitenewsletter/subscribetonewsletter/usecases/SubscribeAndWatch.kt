package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import arrow.core.Either
import arrow.core.computations.either
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Subscriber
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Watch
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SubscriberRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SuburbRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.WatchRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.validations.NewSubscriptionValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Service
class SubscribeAndWatch(
    @Autowired val newSubscriptionValidator: NewSubscriptionValidator,
    @Autowired val subscriberRepo: SubscriberRepository,
    @Autowired val watchRepo: WatchRepository,
    @Autowired val suburbsRepo: SuburbRepository,
    @Autowired val transactionManager: PlatformTransactionManager
) {
    suspend fun call(newSubscriptionDTO: NewSubscriptionDTO): Either<SubscribeAndWatchError, Subscriber> = either {

        val validatedNewSubscriptionData = newSubscriptionValidator.validate(newSubscriptionDTO).toEither().bind()
        val savedSubscriber = storeSubscriberAndWatches(validatedNewSubscriptionData)

        // 4. TODO: Send welcome email
        // 5. TODO: Send first exposure site email (may or may not be the same as the one above)
        savedSubscriber
    }

    private fun storeSubscriberAndWatches(subscriptionData: NewSubscriptionDTO): Subscriber {
        // TODO: move this transaction code to a base/transaction provider
        val transactionTemplate = TransactionTemplate(transactionManager)
        val subscriber = transactionTemplate.execute { status ->

            val savedSubscriber = subscriberRepo.save(
                Subscriber(email = subscriptionData.subscriberData.email)
            )

            // Create watches
            val suburbs = suburbsRepo.findAllById(subscriptionData.suburbsToWatchData.map { it.id })
            val savedWatches = watchRepo.saveAll(
                suburbs.map { Watch(subscriber = savedSubscriber, suburb = it) }
            )

            savedSubscriber
        }
        return subscriber!!
    }
}