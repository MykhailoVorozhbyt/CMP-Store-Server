package com.store.component.customer.data.mapper

import com.store.component.customer.model.CustomerError
import com.store.core.network.utils.toDataError
import org.cmp.store.network.NetworkError

internal fun NetworkError.toCustomerError(): CustomerError = when (this) {
    NetworkError.CUSTOMER_NOT_FOUND, NetworkError.NOT_FOUND -> CustomerError.NotFound
    else -> CustomerError.Common(toDataError())
}
