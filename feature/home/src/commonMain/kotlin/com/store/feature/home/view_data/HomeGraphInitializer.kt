package com.store.feature.home.view_data

import com.store.feature.home.mappers.toViewData
import com.store.component.customer.usecase.ReadCustomerUseCase
import com.store.component.product.usecase.ReadProductsByIdsUseCase
import com.store.core.domain.ApiResult
import com.store.core.presentation.ui.base.ActionHandlerScope
import com.store.core.presentation.ui.base.ViewDataInitializer
import com.store.core.presentation.utils.RequestState
import com.store.core.resources.Res
import com.store.core.resources.common_error_customer_read
import com.store.core.resources.common_error_product_read
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.jetbrains.compose.resources.getString

class HomeGraphInitializer(
    private val readProductsByIdsUseCase: ReadProductsByIdsUseCase,
    private val readCustomerUseCase: ReadCustomerUseCase,
) : ViewDataInitializer.Scoped<HomeGraphViewData> {

    override fun initialize(ctx: ActionHandlerScope<HomeGraphViewData>) {
        ctx.launch {
            ctx.updateViewData { copy(isLoading = true) }
            val customer = readCustomer()
            val totalAmount = when (customer) {
                is RequestState.Success -> calculateTotalAmount(customer.data.cart)
                is RequestState.Error -> RequestState.Error(customer.message)
                else -> RequestState.Loading
            }
            ctx.updateViewData {
                copy(
                    isLoading = false,
                    customer = customer.toViewData(),
                    totalAmountFlow = totalAmount,
                )
            }
        }
    }

    private suspend fun readCustomer(): RequestState<Customer> =
        when (val result = readCustomerUseCase()) {
            is ApiResult.Success -> RequestState.Success(result.data)
            is ApiResult.Error -> RequestState.Error(getString(Res.string.common_error_customer_read))
        }

    private suspend fun calculateTotalAmount(cart: List<CartItem>): RequestState<Double> {
        val productIds = cart.map { it.productId }.distinct()
        if (productIds.isEmpty()) return RequestState.Success(0.0)
        return when (val result = readProductsByIdsUseCase(productIds)) {
            is ApiResult.Success -> {
                val pricesById = result.data.associate { it.id to it.price }
                RequestState.Success(
                    cart.sumOf { item -> (pricesById[item.productId] ?: 0.0) * item.quantity }
                )
            }
            is ApiResult.Error -> RequestState.Error(getString(Res.string.common_error_product_read))
        }
    }
}
