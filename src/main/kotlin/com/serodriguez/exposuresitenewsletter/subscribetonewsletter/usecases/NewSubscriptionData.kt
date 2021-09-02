package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

/* Unfortunately, fields need to be vars for spring to map form params
* to fields */
data class NewSubscriptionData(
    var subscriberData: SubscriberData = SubscriberData(),
    var suburbsToWatchData: List<SuburbData> = listOf(SuburbData(1))
)

data class SubscriberData(var email: String = "")

data class SuburbData(var id: Int)
