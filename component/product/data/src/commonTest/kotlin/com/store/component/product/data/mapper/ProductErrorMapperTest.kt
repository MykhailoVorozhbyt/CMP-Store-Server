package com.store.component.product.data.mapper

import com.store.component.product.model.ProductError
import com.store.core.domain.DataError
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductErrorMapperTest {

    @Test
    fun missing_product_is_not_found() {
        assertEquals(ProductError.NotFound, NetworkError.PRODUCT_NOT_FOUND.toProductError())
        assertEquals(ProductError.NotFound, NetworkError.NOT_FOUND.toProductError())
    }

    @Test
    fun unknown_category_is_invalid_category() {
        assertEquals(ProductError.InvalidCategory, NetworkError.INVALID_PRODUCT_CATEGORY.toProductError())
    }

    @Test
    fun every_other_code_is_delegated_to_the_common_data_error() {
        assertEquals(ProductError.Common(DataError.Server), NetworkError.SERVER_ERROR.toProductError())
        assertEquals(ProductError.Common(DataError.Unknown), NetworkError.CUSTOMER_NOT_FOUND.toProductError())
    }
}
