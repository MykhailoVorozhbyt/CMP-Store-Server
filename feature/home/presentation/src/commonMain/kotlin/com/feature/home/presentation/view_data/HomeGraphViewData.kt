package com.feature.home.presentation.view_data

import androidx.compose.runtime.Immutable
import com.store.core.presentation.utils.RequestState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class HomeGraphViewData(
    val isLoading: Boolean = false,
    val customer: RequestState<CustomerViewData> = RequestState.Loading,
    val totalAmountFlow: RequestState<Double> = RequestState.Loading
)

@Immutable
data class CustomerViewData(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val phoneNumber: PhoneNumberViewData? = null,
    val cart: ImmutableList<CartItemViewData> = persistentListOf(),
    val isAdmin: Boolean = false
)

@Immutable
data class PhoneNumberViewData(
    val dialCode: Int,
    val number: String
)

@Immutable
data class CartItemViewData(
    val id: String,
    val productId: String,
    val flavor: String? = null,
    val quantity: Int
)