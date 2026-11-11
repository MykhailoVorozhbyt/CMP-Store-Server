package com.store.core.data.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.store.core.data.database.StoreDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val databaseBuilderModule: Module

val databaseModule = module {
    includes(databaseBuilderModule)
    single<StoreDatabase> {
        get<RoomDatabase.Builder<StoreDatabase>>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<StoreDatabase>().customerDao() }
}
