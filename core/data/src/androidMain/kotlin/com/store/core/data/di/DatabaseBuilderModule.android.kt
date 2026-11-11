package com.store.core.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.store.core.data.database.DATABASE_NAME
import com.store.core.data.database.StoreDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val databaseBuilderModule: Module = module {
    single<RoomDatabase.Builder<StoreDatabase>> { androidDatabaseBuilder(androidContext()) }
}

private fun androidDatabaseBuilder(context: Context): RoomDatabase.Builder<StoreDatabase> {
    val appContext = context.applicationContext
    return Room.databaseBuilder<StoreDatabase>(
        context = appContext,
        name = appContext.getDatabasePath(DATABASE_NAME).absolutePath,
    )
}
