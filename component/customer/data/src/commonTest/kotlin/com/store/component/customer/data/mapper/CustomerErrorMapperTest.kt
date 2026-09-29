package com.store.component.customer.data.mapper

import com.store.component.customer.model.CustomerError
import com.store.core.domain.DataError
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomerErrorMapperTest {

    @Test
    fun customer_not_found_is_the_component_specific_error() {
        assertEquals(CustomerError.NotFound, NetworkError.CUSTOMER_NOT_FOUND.toCustomerError())
    }

    @Test
    fun a_bare_404_is_also_not_found() {
        assertEquals(CustomerError.NotFound, NetworkError.NOT_FOUND.toCustomerError())
    }

    @Test
    fun every_other_code_is_delegated_to_the_common_data_error() {
        assertEquals(CustomerError.Common(DataError.NoInternet), NetworkError.NO_INTERNET.toCustomerError())
        assertEquals(CustomerError.Common(DataError.Unauthorized), NetworkError.UNAUTHORIZED.toCustomerError())
        assertEquals(CustomerError.Common(DataError.Unknown), NetworkError.INVALID_CREDENTIALS.toCustomerError())
    }
}
