package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import arrow.core.Either
import arrow.core.computations.either
import com.serodriguez.exposuresitenewsletter.base.TransactionProvider
import com.serodriguez.exposuresitenewsletter.base.mail.MailSender
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Subscriber
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Watch
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SubscriberRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SuburbRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.WatchRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.validations.NewSubscriptionValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Service

@Service
class SubscribeAndWatch(
    @Autowired val newSubscriptionValidator: NewSubscriptionValidator,
    @Autowired val subscriberRepo: SubscriberRepository,
    @Autowired val watchRepo: WatchRepository,
    @Autowired val suburbsRepo: SuburbRepository,
    @Autowired val transactionProvider: TransactionProvider,
    @Autowired val mailSender: MailSender,
) {
    suspend fun call(newSubscriptionDTO: NewSubscriptionDTO): Either<SubscribeAndWatchError, Subscriber> = either {

        val validatedNewSubscriptionData = newSubscriptionValidator.validate(newSubscriptionDTO).toEither().bind()
        val savedSubscriber = storeSubscriberAndWatches(validatedNewSubscriptionData)
        sendWelcomeEmail(savedSubscriber.email)
        // 5. TODO: Send first exposure site email (may or may not be the same as the one above)
        savedSubscriber
    }

    private fun storeSubscriberAndWatches(subscriptionData: NewSubscriptionDTO): Subscriber {
        val subscriber = transactionProvider.executeGettingResult() { status ->
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
        return subscriber
    }

    private fun sendWelcomeEmail(subscriberEmail: String) {
        // TODO: use cases should not know about SimpleMailMessage, push this out to base.MelSender
        val mailMessage = SimpleMailMessage()
        mailMessage.setFrom("no-reply@exposure-site-newsletter.com")
        mailMessage.setTo(subscriberEmail)
        mailMessage.setSubject("Thank you for registering to the newsletter")
        mailMessage.setText("This is the body of the welcome email")
        mailSender.send(mailMessage)
    }
}