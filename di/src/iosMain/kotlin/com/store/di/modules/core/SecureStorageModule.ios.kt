package com.store.di.modules.core

import com.store.core.security.IosSecureStorage
import com.store.core.security.SecureStorage
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val secureStorageModule: Module = module {
    singleOf(::IosSecureStorage).bind(SecureStorage::class)
}
