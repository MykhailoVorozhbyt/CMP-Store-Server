package com.feature.home.presentation.view_data

import com.feature.authentication.domain.usecases.ReadCustomerUseCase
import com.feature.home.domain.usecases.ReadProductsByIdsUseCase
import com.feature.home.presentation.mappers.toViewData
import com.store.core.presentation.ui.base.ActionHandlerScope
import com.store.core.presentation.ui.base.ViewDataInitializer
import com.store.core.presentation.utils.RequestState
import com.store.core.resources.Res
import com.store.core.resources.common_error_customer_read
import com.store.core.resources.common_error_product_read
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.product.Product
import org.jetbrains.compose.resources.getString

class HomeGraphInitializer(
    private val readProductsByIdsUseCase: ReadProductsByIdsUseCase,
    private val readCustomerUseCase: ReadCustomerUseCase,
) : ViewDataInitializer.Scoped<HomeGraphViewData> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun initialize(ctx: ActionHandlerScope<HomeGraphViewData>) {
        val customer = readCustomer(ctx)
        val products = getProducts(customer)

        val cartItemsWithProducts = combine(customer, products, ::buildCartItemsWithProducts)
        val totalAmount = cartItemsWithProducts.map(::calculateTotalAmount)

        ctx.launch {
            combine(customer, products, cartItemsWithProducts, totalAmount) {
                    customerState,
                    productsState,
                    _,
                    totalAmountState,
                ->
                HomeGraphViewData(
                    isLoading = customerState.isLoading() || productsState.isLoading(),
                    customer = customerState.toViewData(),
                    totalAmountFlow = totalAmountState,
                )
            }.collect { viewData ->
                ctx.updateViewData { viewData }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getProducts(customer: StateFlow<RequestState<Customer>>): Flow<RequestState<List<Product>>> =
        customer
            .flatMapLatest { customerState ->
                when {
                    customerState.isSuccess() -> {
                        val productIds = customerState.successData().cart
                            .map { it.productId }
                            .toSet()
                            .toList()
                        if (productIds.isEmpty()) {
                            flowOf(RequestState.Success(emptyList()))
                        } else {
                            readProductsByIds(productIds)
                        }
                    }

                    customerState.isError() -> flowOf(RequestState.Error(customerState.errorMessage()))
                    else -> flowOf(RequestState.Loading)
                }
            }

    private fun readCustomer(ctx: ActionHandlerScope<HomeGraphViewData>): StateFlow<RequestState<Customer>> =
        readCustomerUseCase()
            .map { result ->
                result.fold(
                    onSuccess = { RequestState.Success(it) },
                    onFailure = {
                        RequestState.Error(
                            it.message ?: getString(Res.string.common_error_customer_read)
                        )
                    },
                )
            }
            .stateIn(
                scope = ctx.scope,
                started = SharingStarted.Eagerly,
                initialValue = RequestState.Loading,
            )

    private fun readProductsByIds(productIds: List<String>): Flow<RequestState<List<Product>>> =
        readProductsByIdsUseCase(productIds)
            .map { result ->
                result.fold(
                    onSuccess = { RequestState.Success(it) },
                    onFailure = {
                        RequestState.Error(
                            it.message
                                ?: getString(Res.string.common_error_product_read)
                        )
                    },
                )
            }

    private fun buildCartItemsWithProducts(
        customerState: RequestState<Customer>,
        productsState: RequestState<List<Product>>,
    ): RequestState<List<Pair<CartItem, Product>>> {
        return when {
            customerState.isSuccess() && productsState.isSuccess() -> {
                val cart = customerState.successData().cart
                val productsById = productsState.successData().associateBy { it.id }
                val result = cart.mapNotNull { cartItem ->
                    productsById[cartItem.productId]?.let { cartItem to it }
                }
                RequestState.Success(result)
            }

            customerState.isError() -> RequestState.Error(customerState.errorMessage())
            productsState.isError() -> RequestState.Error(productsState.errorMessage())
            else -> RequestState.Loading
        }
    }

    private fun calculateTotalAmount(
        cartItemsWithProductsState: RequestState<List<Pair<CartItem, Product>>>,
    ): RequestState<Double> {
        return when {
            cartItemsWithProductsState.isSuccess() -> {
                val totalPrice = cartItemsWithProductsState.successData()
                    .sumOf { (cartItem, product) -> product.price * cartItem.quantity }
                RequestState.Success(totalPrice)
            }

            cartItemsWithProductsState.isError() -> {
                RequestState.Error(cartItemsWithProductsState.errorMessage())
            }

            else -> RequestState.Loading
        }
    }

}
