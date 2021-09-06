package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import java.util.UUID

/* Unfortunately, fields need to be vars for spring to map form params
* to fields */
data class NewSubscriptionDTO(
    var subscriberData: SubscriberData = SubscriberData(),
    var suburbsToWatchData: List<SuburbData> = listOf()
)

data class SubscriberData(var email: String = "")

data class SuburbData(var id: UUID)
