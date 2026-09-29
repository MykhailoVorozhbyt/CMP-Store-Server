package com.store.component.product.model

import com.store.core.domain.DataError

sealed interface ProductError {
    data object NotFound : ProductError
    data object InvalidCategory : ProductError
    data class Common(val error: DataError) : ProductError
}
