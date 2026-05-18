package org.cmp.store.utils

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectGetWidth
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
actual fun getScreenWidth(): Float {
    val bounds: CValue<CGRect> = UIScreen.mainScreen.bounds
    return CGRectGetWidth(bounds).toFloat()
}