package com.store.di.modules.core

import com.store.core.security.JvmSecureStorage
import com.store.core.security.SecureStorage
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual val secureStorageModule: Module = module {
    single<SecureStorage> { JvmSecureStorage(File(System.getProperty("user.home"), ".store_app")) }
}
