package org.cmp.store

import android.app.Application
import com.store.di.initializeKoin
import org.cmp.store.di.appViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

open class StoreApp(
    private vararg val appModules: Module
) : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin(*appModules, appViewModelModule) {
            androidContext(this@StoreApp)
        }
    }
}
