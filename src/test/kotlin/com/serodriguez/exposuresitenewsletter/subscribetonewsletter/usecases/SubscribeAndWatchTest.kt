package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import arrow.core.nonEmptyListOf
import com.serodriguez.exposuresitenewsletter.base.ValidationError
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Subscriber
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SubscriberRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import support.BaseIntegrationTest

internal class SubscribeAndWatchTest(
    @Autowired val subscribeAndWatch: SubscribeAndWatch,
    @Autowired val subscriberRepo: SubscriberRepository
) : BaseIntegrationTest() {

    @Test
    fun `fails validation when email is taken`() = runBlocking<Unit> {
        subscriberRepo.save(Subscriber(email = "foo@example.com"))

        val newSubscriptionData = NewSubscriptionData(
            SubscriberData(email = "foo@example.com"),
            listOf(SuburbData(2000), SuburbData(4000))
        )

        val result = subscribeAndWatch.call(newSubscriptionData)
        assertTrue(result.isLeft())
        result.mapLeft { error ->
            assertTrue(error is SubscribeAndWatchError.NotValid)
            val validationErrorWrapper = error as SubscribeAndWatchError.NotValid
            val expectedErrorReasons = nonEmptyListOf(
                ValidationError(
                    property = "subscriberData.email",
                    value = "foo@example.com",
                    message = "is already taken"
                ),
            )
            assertTrue(expectedErrorReasons == validationErrorWrapper.reasons)
        }
    }

    @Test
    fun `stores the subscriber`() = runBlocking<Unit> {
        val newSubscriptionData = NewSubscriptionData(
            SubscriberData(email = "foo@example.com"),
            listOf(SuburbData(3000), SuburbData(3008))
        )
        subscribeAndWatch.call(newSubscriptionData)

        val subscribers = subscriberRepo.findAll()
        assertEquals(1, subscribers.count())
        assertEquals("foo@example.com", subscribers.first().email)
    }
}