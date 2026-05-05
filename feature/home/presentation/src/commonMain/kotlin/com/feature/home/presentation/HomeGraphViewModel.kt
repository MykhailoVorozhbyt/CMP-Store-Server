package com.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feature.authentication.domain.usecases.ReadCustomerUseCase
import com.feature.authentication.domain.usecases.SignOutUseCase
import com.feature.home.domain.repository.ProductRepository
import com.feature.home.presentation.view_data.HomeViewData
import com.store.core.presentation.core.di.coroutines.IoDispatcher
import com.store.core.presentation.core.di.coroutines.MainDispatcher
import com.store.core.presentation.core.viewmodel.BaseActionHandleViewModel
import com.store.core.presentation.ui.ViewAction
import com.store.core.presentation.utils.RequestState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeGraphViewModel(
    private val productRepository: ProductRepository,
    private val readCustomerUseCase: ReadCustomerUseCase,
    private val signOutUseCase: SignOutUseCase,
    @MainDispatcher mainDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : BaseActionHandleViewModel<HomeViewData>(mainDispatcher, ioDispatcher) {

    override val _viewData = MutableStateFlow(HomeViewData())

    override suspend fun handleViewAction(action: ViewAction) {
        // implement after HomeViewData fields are defined
    }

    init {
        println("Initializing....ViewModel")
    }

    val customer = readCustomerUseCase()
        .map { result ->
            result.fold(
                onSuccess = { RequestState.Success(it) },
                onFailure = { RequestState.Error(it.message ?: CUSTOMER_READ_ERROR) }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RequestState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val products = customer
        .flatMapLatest { customerState ->
            if (customerState.isSuccess()) {
                val productIds = customerState.getSuccessData().cart.map { it.productId }.toSet()
                if (productIds.isNotEmpty()) {
                    productRepository.readProductsByIdsFlow(productIds.toList())
                        .map { result ->
                            result.fold(
                                onSuccess = { RequestState.Success(it) },
                                onFailure = { RequestState.Error(it.message ?: PRODUCT_READ_ERROR) }
                            )
                        }
                } else flowOf(RequestState.Success(emptyList()))
            } else if (customerState.isError()) {
                flowOf(RequestState.Error(customerState.getErrorMessage()))
            } else flowOf(RequestState.Loading)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val cartItemsWithProducts = combine(customer, products) { customerState, productsState ->
        when {
            customerState.isSuccess() && productsState.isSuccess() -> {
                val cart = customerState.getSuccessData().cart
                val products = productsState.getSuccessData()

                val result = cart.mapNotNull { cartItem ->
                    val product = products.find { it.id == cartItem.productId }
                    product?.let { cartItem to it }
                }

                RequestState.Success(result)
            }

            customerState.isError() -> RequestState.Error(customerState.getErrorMessage())
            productsState.isError() -> RequestState.Error(productsState.getErrorMessage())

            else -> RequestState.Loading
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalAmountFlow = cartItemsWithProducts
        .flatMapLatest { data ->
            if (data.isSuccess()) {
                val items = data.getSuccessData()
                val cartItems = items.map { it.first }
                val products = items.map { it.second }.associateBy { it.id }

                val totalPrice = cartItems.sumOf { cartItem ->
                    val productPrice = products[cartItem.productId]?.price ?: 0.0
                    productPrice * cartItem.quantity
                }

                flowOf(RequestState.Success(totalPrice))
            } else if (data.isError()) flowOf(RequestState.Error(data.getErrorMessage()))
            else flowOf(RequestState.Loading)
        }

    fun signOut(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            signOutUseCase()
                .onSuccess { onSuccess() }
                .onFailure { onError(it.message ?: SIGN_OUT_ERROR) }
        }
    }

    private companion object {
        const val CUSTOMER_READ_ERROR = "Failed to read customer."
        const val PRODUCT_READ_ERROR = "Failed to read products."
        const val SIGN_OUT_ERROR = "Failed to sign out."
    }
}
