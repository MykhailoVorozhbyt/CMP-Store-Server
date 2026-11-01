package com.store.feature.profile.handler

import com.store.component.customer.model.CustomerError
import com.store.core.resources.Res as CoreRes
import com.store.core.resources.common_error_no_internet
import com.store.core.resources.common_error_unknown
import com.store.feature.profile.resources.Res
import com.store.feature.profile.resources.profile_error_customer_not_found
import com.store.feature.profile.resources.profile_update_success
import org.jetbrains.compose.resources.getString

interface ProfileMessageHandler {
    suspend fun handle(error: CustomerError): String
    suspend fun updateSuccess(): String
    suspend fun noInternet(): String
}

class ProfileMessageHandlerImpl : ProfileMessageHandler {

    override suspend fun handle(error: CustomerError): String = when (error) {
        CustomerError.NotFound -> getString(Res.string.profile_error_customer_not_found)
        is CustomerError.Common -> getString(CoreRes.string.common_error_unknown)
    }

    override suspend fun updateSuccess(): String = getString(Res.string.profile_update_success)

    override suspend fun noInternet(): String = getString(CoreRes.string.common_error_no_internet)
}
