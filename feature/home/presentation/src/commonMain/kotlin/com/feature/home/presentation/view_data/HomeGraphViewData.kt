package com.feature.home.presentation.view_data

import androidx.compose.runtime.Immutable
import com.store.core.presentation.utils.RequestState
import org.cmp.store.domain.customer.Customer

@Immutable
data class HomeGraphViewData(
    val isLoading: Boolean = false,
    val customer: RequestState<Customer> = RequestState.Loading,
    val totalAmountFlow: RequestState<Double> = RequestState.Loading
)
