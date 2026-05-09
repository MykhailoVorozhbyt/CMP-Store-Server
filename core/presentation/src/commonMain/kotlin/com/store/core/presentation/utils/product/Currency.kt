package com.store.core.presentation.utils.product

import com.store.core.resources.Res
import com.store.core.resources.currency_euro
import com.store.core.resources.currency_franc
import com.store.core.resources.currency_uah
import com.store.core.resources.currency_usd
import com.store.core.resources.ic_currency_euro
import com.store.core.resources.ic_currency_franc
import com.store.core.resources.ic_currency_uah
import com.store.core.resources.ic_currency_usd
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class Currency(
    val id: Long,
    val textResource: StringResource,
    val iconResource: DrawableResource
) {
    UAH(1, Res.string.currency_uah, Res.drawable.ic_currency_uah),
    USD(2, Res.string.currency_usd, Res.drawable.ic_currency_usd),
    EURO(3, Res.string.currency_euro, Res.drawable.ic_currency_euro),
    FRANC(4, Res.string.currency_franc, Res.drawable.ic_currency_franc);

    companion object {
        fun getCurrencyById(id: Long): Currency = CurrencyList.firstOrNull { it.id == id } ?: UAH
//        fun getCurrencyChipIndexByTextId(id: Int) =
//            CurrencyList.firstOrNull { it.textId == id } ?: UAH
    }

}

val CurrencyList = listOf(Currency.UAH, Currency.USD, Currency.EURO, Currency.FRANC)