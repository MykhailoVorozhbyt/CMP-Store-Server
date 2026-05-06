package org.cmp.store.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object Auth : Screen, NavKey

    @Serializable
    data class HomeGraph(
        val welcomeMessage: String? = null
    ) : Screen, NavKey

    @Serializable
    data object ProductsOverview : Screen

    @Serializable
    data object Cart : Screen

    @Serializable
    data object Categories : Screen

    @Serializable
    data class CategorySearch(
        val category: String
    ) : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object AdminPanel : Screen

    @Serializable
    data object ContactUs : Screen

    @Serializable
    data class ManageProduct(
        val id: String? = null,
    ) : Screen

    @Serializable
    data class Details(
        val id: String
    ) : Screen

    @Serializable
    data class Checkout(
        val totalAmount: String
    ) : Screen

    @Serializable
    data class PaymentCompleted(
        val isSuccess: Boolean? = null,
        val error: String? = null,
        val token: String? = null
    ) : Screen
}