package com.store.core.presentation.theme.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.store.core.presentation.resources.Res
import com.store.core.presentation.resources.roboto_condensed_bold
import com.store.core.presentation.resources.roboto_condensed_light
import com.store.core.presentation.resources.roboto_condensed_medium
import com.store.core.presentation.resources.roboto_condensed_regular
import org.jetbrains.compose.resources.Font

class StoreTypography(
    val defaultFontFamily: FontFamily,
    val regular: TextStyle,
    val bold: TextStyle,
    val light: TextStyle,
    val tab: TextStyle,
    val textField: TextStyle,
    val topAppBar: TextStyle,
    // ---- FROM DESIGN SYSTEM ---- //@formatter:off
    val rxs: TextStyle, // R/XS Normal 10px
    val rs: TextStyle,  // R/S  Normal 12px
    val rm: TextStyle,  // R/M  Normal 14px
    val rl: TextStyle,  // R/L  Normal 16px
    val bs: TextStyle,  // B/S  Bold   12px
    val bm: TextStyle,  // B/M  Bold   14px
    val bx: TextStyle,  // B/X  Bold   16px
    val bxl: TextStyle, // B/XL Bold   18px
    val medium: TextStyle
    //@formatter:on
) {
    constructor(
        defaultFontFamily: FontFamily = FontFamily(),
        regular: TextStyle = TextStyle(fontWeight = FontWeight.Normal),
        bold: TextStyle = TextStyle(fontWeight = FontWeight.Bold),
        light: TextStyle = TextStyle(fontWeight = FontWeight.Light),
        tab: TextStyle = TextStyle(fontWeight = FontWeight.Bold),
        textField: TextStyle = TextStyle(fontWeight = FontWeight.Bold),
        bs: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
        bm: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp),
        bx: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
        rxs: TextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 10.sp),
        rs: TextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp),
        rm: TextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
        rl: TextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
        bxl: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
        medium: TextStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
    ) : this(
        defaultFontFamily = defaultFontFamily,
        regular = regular.withDefaultFontFamily(defaultFontFamily),
        bold = bold.withDefaultFontFamily(defaultFontFamily),
        light = light.withDefaultFontFamily(defaultFontFamily),
        tab = tab.withDefaultFontFamily(defaultFontFamily),
        textField = textField.withDefaultFontFamily(defaultFontFamily),
        topAppBar = bxl.withDefaultFontFamily(defaultFontFamily),
        rxs = rxs.withDefaultFontFamily(defaultFontFamily),
        rs = rs.withDefaultFontFamily(defaultFontFamily),
        rm = rm.withDefaultFontFamily(defaultFontFamily),
        rl = rl.withDefaultFontFamily(defaultFontFamily),
        bs = bs.withDefaultFontFamily(defaultFontFamily),
        bm = bm.withDefaultFontFamily(defaultFontFamily),
        bx = bx.withDefaultFontFamily(defaultFontFamily),
        bxl = bxl.withDefaultFontFamily(defaultFontFamily),
        medium = medium.withDefaultFontFamily(defaultFontFamily)
    )

    companion object {
        @Composable
        fun init(): StoreTypography = StoreTypography(AppFontFamily)
    }
}

val AppFontFamily
    @Composable get() = FontFamily(
        Font(Res.font.roboto_condensed_bold, weight = FontWeight.Bold),
        Font(Res.font.roboto_condensed_regular, weight = FontWeight.Normal),
        Font(Res.font.roboto_condensed_light, weight = FontWeight.Light),
        Font(Res.font.roboto_condensed_medium, weight = FontWeight.Medium)
    )

private fun TextStyle.withDefaultFontFamily(default: FontFamily): TextStyle {
    return if (fontFamily != null) this else copy(fontFamily = default)
}

val LocalStoreTypography = staticCompositionLocalOf<StoreTypography> {
    error("No font provided")
}