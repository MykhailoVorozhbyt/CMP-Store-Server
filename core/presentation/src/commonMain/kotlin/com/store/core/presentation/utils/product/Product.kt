package com.store.core.presentation.utils.product

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.store.core.presentation.theme.StoreTheme
import org.cmp.store.domain.product.ProductCategory

@Composable
fun ProductCategory.color(): Color = when (this) {
    ProductCategory.Protein -> StoreTheme.color.category1
    ProductCategory.Creatine -> StoreTheme.color.category2
    ProductCategory.PreWorkout -> StoreTheme.color.category3
    ProductCategory.Gainers -> StoreTheme.color.category4
    ProductCategory.Accessories -> StoreTheme.color.category5
    ProductCategory.Unknown -> StoreTheme.color.textSecondary
}