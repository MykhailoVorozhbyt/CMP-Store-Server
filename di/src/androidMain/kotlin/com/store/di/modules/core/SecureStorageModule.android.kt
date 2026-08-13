package com.store.di.modules.core

import com.store.core.security.AndroidSecureStorage
import com.store.core.security.SecureStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val secureStorageModule: Module = module {
    single<SecureStorage> { AndroidSecureStorage(androidContext()) }
}