package com.store.athletica_plus

import com.store.athletica_plus.theme.di.athleticaPlusThemeModule
import org.cmp.store.StoreApp

class AthleticaPlusApp : StoreApp(
    athleticaPlusThemeModule
) {
    override fun onCreate() {
        super.onCreate()
    }
}