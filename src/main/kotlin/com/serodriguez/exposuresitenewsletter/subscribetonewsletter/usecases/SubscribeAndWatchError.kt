package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import arrow.core.Nel
import com.serodriguez.exposuresitenewsletter.base.ValidationError

sealed class SubscribeAndWatchError {
    class NotValid(val reasons: Nel<ValidationError>): SubscribeAndWatchError()
    object NotAuthorized: SubscribeAndWatchError()
}
