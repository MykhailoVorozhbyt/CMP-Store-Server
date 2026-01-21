package com.store.core.presentation.theme.typography

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.store.core.presentation.R

class StoreTypography(
    val regular: TextStyle,
    val bold: TextStyle,
    val light: TextStyle,
    val tab: TextStyle,
    val textField: TextStyle,
    val topAppBar: TextStyle,
    // ---- FROM DESIGN SYSTEM ---- //@formatter:off
    val rxs: TextStyle, // R/XS Normal 400, 10px
    val rs: TextStyle,  // R/S  Normal 400, 12px
    val rm: TextStyle,  // R/M  Normal 400, 14px
    val rl: TextStyle,  // R/L  Normal 400, 16px
    val bs: TextStyle,  // B/S  Bold   700, 12px
    val bm: TextStyle,  // B/M  Bold   700, 14px
    val bx: TextStyle,  // B/X  Bold   700, 16px
    val bxl: TextStyle, // B/XL Bold   700, 18px
    val medium: TextStyle
    //@formatter:on
) {
    constructor(
        defaultFontFamily: FontFamily = roboto,
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
        b3xl: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp),
        medium: TextStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
    ) : this(
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
}

private val roboto = FontFamily(
    Font(R.font.roboto_condensed_bold, weight = FontWeight.Bold),
    Font(R.font.roboto_condensed_regular, weight = FontWeight.Normal),
    Font(R.font.roboto_condensed_light, weight = FontWeight.Light),
    Font(R.font.roboto_condensed_medium, weight = FontWeight.Medium)
)

private fun TextStyle.withDefaultFontFamily(default: FontFamily): TextStyle {
    return if (fontFamily != null) this else copy(fontFamily = default)
}

val LocalStoreTypography = staticCompositionLocalOf<StoreTypography> {
    error("No font provided")
}