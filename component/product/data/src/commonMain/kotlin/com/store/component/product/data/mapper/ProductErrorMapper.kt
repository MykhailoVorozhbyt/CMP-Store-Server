package com.store.component.product.data.mapper

import com.store.component.product.model.ProductError
import com.store.core.network.utils.toDataError
import org.cmp.store.network.NetworkError

internal fun NetworkError.toProductError(): ProductError = when (this) {
    NetworkError.PRODUCT_NOT_FOUND, NetworkError.NOT_FOUND -> ProductError.NotFound
    NetworkError.INVALID_PRODUCT_CATEGORY -> ProductError.InvalidCategory
    else -> ProductError.Common(toDataError())
}
