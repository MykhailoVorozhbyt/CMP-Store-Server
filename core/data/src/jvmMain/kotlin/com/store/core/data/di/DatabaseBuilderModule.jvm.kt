package com.store.core.data.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.store.core.data.database.DATABASE_NAME
import com.store.core.data.database.StoreDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

private const val STORAGE_DIR = ".store_app"

internal actual val databaseBuilderModule: Module = module {
    single<RoomDatabase.Builder<StoreDatabase>> { jvmDatabaseBuilder() }
}

private fun jvmDatabaseBuilder(): RoomDatabase.Builder<StoreDatabase> {
    val directory = File(System.getProperty("user.home"), STORAGE_DIR).apply { mkdirs() }
    return Room.databaseBuilder<StoreDatabase>(name = File(directory, DATABASE_NAME).absolutePath)
}
