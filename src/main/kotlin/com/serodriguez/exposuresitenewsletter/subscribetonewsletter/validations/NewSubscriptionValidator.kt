package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.validations

import arrow.core.Nel
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.valid
import arrow.core.zip
import com.serodriguez.exposuresitenewsletter.base.ValidationError
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SubscriberRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SuburbRepository
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.NewSubscriptionDTO
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.SubscribeAndWatchError
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.SubscriberData
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.SuburbData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NewSubscriptionValidator(
    @Autowired val subscriberRepo: SubscriberRepository,
    @Autowired val suburbsRepo: SuburbRepository,
) {

    private val emailRegex = Regex(
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
    )

    fun validate(
        newSubscriptionDTO: NewSubscriptionDTO): Validated<SubscribeAndWatchError.NotValid, NewSubscriptionDTO> {
        val maybeValidatedData =
            validateSubscriberData(newSubscriptionDTO.subscriberData).zip(
                validateSuburbsToWatchData(newSubscriptionDTO.suburbsToWatchData)
        ) { _, _ -> newSubscriptionDTO }

        return maybeValidatedData.mapLeft { SubscribeAndWatchError.NotValid(reasons = it) }
    }


    private fun validateSubscriberData(subscriberData: SubscriberData):
        ValidatedNel<ValidationError, SubscriberData> {
        with(subscriberData.email) {
            if (isBlank()) {
                return ValidationError(
                    property = "subscriberData.email",
                    value = this,
                    message = "email cannot be empty"
                ).invalidNel()
            }
            if(!this.matches(emailRegex)) {
                return ValidationError(
                    property = "subscriberData.email",
                    value = this,
                    message = "invalid email format"
                ).invalidNel()
            }
            if (isNotBlank() && subscriberRepo.existsByEmail(this)) {
                return ValidationError(
                    property = "subscriberData.email",
                    value = this,
                    message = "email is already taken"
                ).invalidNel()
            }
            return subscriberData.valid()
        }
    }

    private fun validateSuburbsToWatchData(suburbsToWatch: List<SuburbData>):
        ValidatedNel<ValidationError, List<SuburbData>> {
        if (suburbsToWatch.isEmpty()) {
            return ValidationError(
                property = "suburbsToWatchData",
                value = suburbsToWatch,
                message = "you must select at least one suburb to watch"
            ).invalidNel()
        }

        val watchableSuburbIds = suburbsRepo.findAll().map { it.id!! }
        val invalidSuburbs = mutableListOf<ValidationError>()
        suburbsToWatch.forEachIndexed { index, suburbData ->
            if (!watchableSuburbIds.contains(suburbData.id) ) {
                invalidSuburbs.add(
                    ValidationError(
                        property = "suburbsToWatchData[$index].id",
                        value = suburbData.id,
                        message = "must be a valid id"
                    )
                )
            }
        }

         if (invalidSuburbs.size > 0) {
            return Validated.Invalid(
                Nel.fromListUnsafe(invalidSuburbs)
            )
        }

        return suburbsToWatch.valid()
    }
}