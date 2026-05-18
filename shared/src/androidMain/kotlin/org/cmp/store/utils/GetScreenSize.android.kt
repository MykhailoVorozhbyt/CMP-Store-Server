package org.cmp.store.utils

actual fun getScreenWidth(): Float {
    return android.content.res.Resources.getSystem().displayMetrics.widthPixels /
            android.content.res.Resources.getSystem().displayMetrics.density
}