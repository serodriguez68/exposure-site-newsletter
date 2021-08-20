package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SubscriberRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import support.BaseIntegrationTest

internal class SubscribeAndWatchTest(
    @Autowired val subscribeAndWatch: SubscribeAndWatch,
    @Autowired val subscriberRepo: SubscriberRepository
): BaseIntegrationTest() {

    @Test
    fun `stores the subscriber`() {
        val newSubscriptionData = NewSubscriptionData(
            SubscriberData(email = "foo@exmaple.com"),
            listOf(SuburbData(3000), SuburbData(3008))
        )
        subscribeAndWatch.call(newSubscriptionData)

        val subscribers = subscriberRepo.findAll()
        assertEquals(1,subscribers.count())
        assertEquals("foo@exmaple.com", subscribers.first().email)
    }
}