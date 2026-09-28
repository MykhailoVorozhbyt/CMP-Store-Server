package com.store.core.security.di

import com.store.core.security.JvmSecureStorage
import com.store.core.security.SecureStorage
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

private const val STORAGE_DIR = ".store_app"

actual val secureStorageModule: Module = module {
    single<SecureStorage> { JvmSecureStorage(File(System.getProperty("user.home"), STORAGE_DIR)) }
}
