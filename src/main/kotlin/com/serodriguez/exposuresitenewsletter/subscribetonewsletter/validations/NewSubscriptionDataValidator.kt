package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.validations

import arrow.core.Nel
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.valid
import arrow.core.zip
import com.serodriguez.exposuresitenewsletter.base.ValidationError
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.NewSubscriptionDTO
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.SubscribeAndWatchError
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.SubscriberData
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.SuburbData
import org.springframework.stereotype.Component

@Component
class NewSubscriptionDataValidator {

    fun validate(
        newSubscriptionDTO: NewSubscriptionDTO,
        emailExistenceChecker: (String) -> Boolean
    ): Validated<SubscribeAndWatchError.NotValid, NewSubscriptionDTO> {
        val maybeValidatedData =
            validateSubscriberData(newSubscriptionDTO.subscriberData, emailExistenceChecker).zip(
                validateSuburbsToWatchData(newSubscriptionDTO.suburbsToWatchData)
        ) { _, _ -> newSubscriptionDTO }

        return maybeValidatedData.mapLeft { SubscribeAndWatchError.NotValid(reasons = it) }
    }


    private fun validateSubscriberData(subscriberData: SubscriberData, emailExistenceChecker: (String) -> Boolean):
        ValidatedNel<ValidationError, SubscriberData> {
        with(subscriberData.email) {
            if (isBlank()) {
                return ValidationError(
                    property = "subscriberData.email",
                    value = this,
                    message = "email cannot be empty"
                ).invalidNel()
            }
            if (isNotBlank() && emailExistenceChecker(this)) {
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

        val invalidSuburbs = mutableListOf<ValidationError>()
        suburbsToWatch.forEachIndexed { index, suburbData ->
            if (suburbData.id == null) {
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