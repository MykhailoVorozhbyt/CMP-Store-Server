package com.store.feature.profile

import com.store.component.customer.model.CustomerError
import com.store.feature.profile.handler.ProfileMessageHandler

class FakeProfileMessageHandler : ProfileMessageHandler {
    override suspend fun handle(error: CustomerError): String = "error:$error"
    override suspend fun updateSuccess(): String = UPDATE_SUCCESS
    override suspend fun nothingChanged(): String = NOTHING_CHANGED
    override suspend fun noInternet(): String = NO_INTERNET

    companion object {
        const val UPDATE_SUCCESS = "updated"
        const val NOTHING_CHANGED = "nothing changed"
        const val NO_INTERNET = "no internet"
    }
}
