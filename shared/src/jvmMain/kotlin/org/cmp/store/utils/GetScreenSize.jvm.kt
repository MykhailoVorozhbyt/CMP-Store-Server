package org.cmp.store.utils

import java.awt.Toolkit

actual fun getScreenWidth(): Float {
    return Toolkit.getDefaultToolkit()
        .screenSize
        .width
        .toFloat()
}