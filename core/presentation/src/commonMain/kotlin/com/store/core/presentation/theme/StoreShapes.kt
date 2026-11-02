package com.store.core.presentation.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val LocalStoreShapes = staticCompositionLocalOf<StoreShapes> {
    error("LocalStoreShapes no provided")
}

@Immutable
data class StoreShapes(
    val none: Shape = RectangleShape,
    val extraSmall: Shape = RoundedCornerShape(2.dp),
    val small: Shape = RoundedCornerShape(4.dp),
    val medium: Shape = RoundedCornerShape(8.dp),
    val large: Shape = RoundedCornerShape(12.dp),
    val extraLarge: Shape = RoundedCornerShape(16.dp),
    val full: Shape = CircleShape,
)
