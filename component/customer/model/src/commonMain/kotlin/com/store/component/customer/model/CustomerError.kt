package com.store.component.customer.model

import com.store.core.domain.DataError

sealed interface CustomerError {
    data object NotFound : CustomerError
    data class Common(val error: DataError) : CustomerError
}
