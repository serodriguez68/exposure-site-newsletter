package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import arrow.core.nonEmptyListOf
import com.serodriguez.exposuresitenewsletter.base.ValidationError
import com.serodriguez.exposuresitenewsletter.base.mail.FakeMailServer
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Subscriber
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Suburb
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SubscriberRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SuburbRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.WatchRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import support.BaseIntegrationTest
import java.util.UUID

internal class SubscribeAndWatchTest(
    @Autowired val subscribeAndWatch: SubscribeAndWatch,
    @Autowired val subscriberRepo: SubscriberRepository,
    @Autowired val suburbRepo: SuburbRepository,
    @Autowired val watchesRepo: WatchRepository,
    @Autowired val fakeMailServer: FakeMailServer
) : BaseIntegrationTest(fakeMailServer) {

    @BeforeEach
    fun `create some suburbs`() {
        suburbRepo.saveAll(
            listOf(
                Suburb(postCode = "3000", name = "CBD"),
                Suburb(postCode = "3008", name = "Docklands")
            )
        )
    }

    @Nested
    inner class Validations {
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
        fun `fails when email has invalid format`() = runBlocking<Unit> {
            val newSubscriptionDTO = NewSubscriptionDTO(
                SubscriberData(email = "fooexample.com"),
                allSuburbs()
            )
            val result = subscribeAndWatch.call(newSubscriptionDTO)
            assertTrue(result.isLeft())
            result.mapLeft { error ->
                assertTrue(error is SubscribeAndWatchError.NotValid)
                val validationErrorWrapper = error as SubscribeAndWatchError.NotValid
                assertTrue(validationErrorWrapper.reasons.any { reason -> reason.message == "invalid email format" })
            }
        }

        @Test
        fun `fails validation when no suburbs to watch are provided`() = runBlocking<Unit> {
            val newSubscriptionDTO = NewSubscriptionDTO(
                SubscriberData(email = "foo@example.com"),
                listOf()
            )

            val result = subscribeAndWatch.call(newSubscriptionDTO)
            assertTrue(result.isLeft())
            result.mapLeft { error ->
                assertTrue(error is SubscribeAndWatchError.NotValid)
                val validationErrorWrapper = error as SubscribeAndWatchError.NotValid
                assertTrue(validationErrorWrapper.reasons.size == 1)
                assertTrue(validationErrorWrapper.reasons.first().message == "you must select at least one suburb to watch")
            }
        }

        @Test
        fun `fails when the id of a suburb to watch does not exist`() = runBlocking<Unit> {
            val newSubscriptionDTO = NewSubscriptionDTO(
                SubscriberData(email = "foo@example.com"),
                listOf(SuburbData(UUID.randomUUID()))
            )
            val result = subscribeAndWatch.call(newSubscriptionDTO)
            assertTrue(result.isLeft())
            result.mapLeft { error ->
                assertTrue(error is SubscribeAndWatchError.NotValid)
                val validationErrorWrapper = error as SubscribeAndWatchError.NotValid
                assertTrue(validationErrorWrapper.reasons.size == 1)
                val reason = validationErrorWrapper.reasons.first()
                assertTrue(reason.property == "suburbsToWatchData[0].id")
                assertTrue(reason.message == "must be a valid id")
            }
        }
    }

    @Test
    fun `stores the subscriber and the selected suburbs to watch`() = runBlocking<Unit> {
        val newSubscriptionDTO = NewSubscriptionDTO(
            SubscriberData(email = "foo@example.com"),
            allSuburbs(),
        )
        subscribeAndWatch.call(newSubscriptionDTO)

        val subscribers = subscriberRepo.findAll()
        assertEquals(1, subscribers.count())
        val theSubscriber = subscribers.first()
        assertEquals("foo@example.com", theSubscriber.email)

        val storedWatches = watchesRepo.findAll()
        assertEquals(2, storedWatches.count())
        assertTrue(storedWatches.all { it.subscriber.id == theSubscriber.id })
        assertTrue(storedWatches.map { it.suburb.postCode }.contains("3000"))
        assertTrue(storedWatches.map { it.suburb.postCode }.contains("3008"))
    }

    @Test
    fun `sends welcome email`() = runBlocking<Unit> {
        val newSubscriptionDTO = NewSubscriptionDTO(
            SubscriberData(email = "foo@example.com"),
            allSuburbs(),
        )
        subscribeAndWatch.call(newSubscriptionDTO)
        val receivedMessages = fakeMailServer.messages()
        assertEquals(1, receivedMessages.size)
        val message =  receivedMessages.first()
        assertEquals("Thank you for registering to the newsletter", message.subject)
        assertEquals(1, message.to?.size)
        assertEquals("foo@example.com", message.to?.first())
        assertTrue(message.text?.contains("body", ignoreCase = true)!!)
    }

    // Left here to play with transactions
    // @Test
    // fun `make sure transactions work`() = runBlocking {
    //     val newSubscriptionDTO = NewSubscriptionDTO(
    //         SubscriberData(email = "foo@example.com"),
    //         allSuburbs()
    //     )
    //     try {
    //         subscribeAndWatch.call(newSubscriptionDTO)
    //     } catch (e: RuntimeException) {
    //         // Ignored
    //     }
    //     assertEquals(0, subscriberRepo.findAll().count())
    //     assertEquals(0, watchesRepo.findAll().count())
    // }

    private fun allSuburbs(): List<SuburbData> = suburbRepo.findAll().map { SuburbData(it.id!!) }
}