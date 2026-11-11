package com.store.core.data.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.store.core.data.database.DATABASE_NAME
import com.store.core.data.database.StoreDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal actual val databaseBuilderModule: Module = module {
    single<RoomDatabase.Builder<StoreDatabase>> { iosDatabaseBuilder() }
}

private fun iosDatabaseBuilder(): RoomDatabase.Builder<StoreDatabase> =
    Room.databaseBuilder<StoreDatabase>(name = "${documentDirectory()}/$DATABASE_NAME")

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(directory?.path)
}
