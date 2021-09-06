package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import arrow.core.nonEmptyListOf
import com.serodriguez.exposuresitenewsletter.base.ValidationError
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Subscriber
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Suburb
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SubscriberRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SuburbRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import support.BaseIntegrationTest

internal class SubscribeAndWatchTest(
    @Autowired val subscribeAndWatch: SubscribeAndWatch,
    @Autowired val subscriberRepo: SubscriberRepository,
    @Autowired val suburbRepo: SuburbRepository
) : BaseIntegrationTest() {

    @BeforeEach
    fun `create some suburbs`() {
        suburbRepo.saveAll(
            listOf(
                Suburb(postCode =  "3000", name = "CBD"),
                Suburb(postCode = "3008", name = "Docklands")
            )
        )
    }

    @Test
    fun `fails validation when email is taken`() = runBlocking<Unit> {
        subscriberRepo.save(Subscriber(email = "foo@example.com"))

        val newSubscriptionDTO = NewSubscriptionDTO(
            SubscriberData(email = "foo@example.com"),
            allSuburbs()
        )

        val result = subscribeAndWatch.call(newSubscriptionDTO)
        assertTrue(result.isLeft())
        result.mapLeft { error ->
            assertTrue(error is SubscribeAndWatchError.NotValid)
            val validationErrorWrapper = error as SubscribeAndWatchError.NotValid
            val expectedErrorReasons = nonEmptyListOf(
                ValidationError(
                    property = "subscriberData.email",
                    value = "foo@example.com",
                    message = "email is already taken"
                ),
            )
            assertTrue(expectedErrorReasons == validationErrorWrapper.reasons)
        }
    }

    @Test
    fun `stores the subscriber`() = runBlocking<Unit> {
        val newSubscriptionDTO = NewSubscriptionDTO(
            SubscriberData(email = "foo@example.com"),
            allSuburbs(),
        )
        subscribeAndWatch.call(newSubscriptionDTO)

        val subscribers = subscriberRepo.findAll()
        assertEquals(1, subscribers.count())
        assertEquals("foo@example.com", subscribers.first().email)
    }

    @Test
    fun `make sure transactions work`() = runBlocking {
        val newSubscriptionDTO = NewSubscriptionDTO(
            SubscriberData(email = "foo@example.com"),
            allSuburbs()
        )
        try {
            subscribeAndWatch.call(newSubscriptionDTO)
        } catch (e: RuntimeException) {
            // Ignored
        }
        val subscribers = subscriberRepo.findAll()
        assertEquals(0, subscribers.count())
    }

    private fun allSuburbs(): List<SuburbData> = suburbRepo.findAll().map { SuburbData(it.id!!) }

}