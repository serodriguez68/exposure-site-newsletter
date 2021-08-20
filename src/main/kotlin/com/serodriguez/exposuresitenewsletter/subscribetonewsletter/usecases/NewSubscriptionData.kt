package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

data class NewSubscriptionData(
    val subscriberData: SubscriberData,
    val suburbsToWatchData: List<SuburbData>
)

data class SubscriberData(val email: String)

data class SuburbData(val id: Int)
