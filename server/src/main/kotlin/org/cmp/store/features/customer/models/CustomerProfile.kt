package org.cmp.store.features.customer.models

import org.cmp.store.domain.customer.PhoneNumber

data class CustomerProfile(
    val firstName: String,
    val lastName: String,
    val city: String?,
    val postalCode: Int?,
    val address: String?,
    val phoneNumber: PhoneNumber?,
)
