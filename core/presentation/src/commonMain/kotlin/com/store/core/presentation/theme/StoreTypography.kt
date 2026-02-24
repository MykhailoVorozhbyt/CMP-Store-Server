package com.store.core.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.store.core.resources.Resources
import org.jetbrains.compose.resources.Font

val LocalStoreTypography = staticCompositionLocalOf<StoreTypography> {
    error("LocalStoreTypography no provided")
}

class StoreTypography(
    val dimens: StoreDimens,
    val defaultFontFamily: FontFamily = FontFamily.Default,
    val regular: TextStyle = TextStyle(fontWeight = FontWeight.Normal),
    val bold: TextStyle = TextStyle(fontWeight = FontWeight.Bold),
    val textField: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = dimens.textField),
    val topAppBar: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = dimens.topAppBarText),
    val rs: TextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = dimens.textS), // R/S  Normal 12px
    val rm: TextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = dimens.textM), // R/M  Normal 14px
    val rl: TextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = dimens.textL), // R/L  Normal 16px
    val bs: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = dimens.textS),   // B/S  Bold   12px
    val bm: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = dimens.textM),   // B/M  Bold   14px
    val bl: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = dimens.textL),   // B/L  Bold   16px
) {
    companion object {
        @Composable
        fun init(): StoreTypography = StoreTypography(dimens = rememberDimens(), defaultFontFamily = AppFontFamily)
    }
}

val AppFontFamily
    @Composable get() = FontFamily(
        Font(Resources.Font.robotoCondensedBold, weight = FontWeight.Bold),
        Font(Resources.Font.robotoCondensedRegular, weight = FontWeight.Normal),
        Font(Resources.Font.robotoCondensedLight, weight = FontWeight.Light),
        Font(Resources.Font.robotoCondensedMedium, weight = FontWeight.Medium)
    )