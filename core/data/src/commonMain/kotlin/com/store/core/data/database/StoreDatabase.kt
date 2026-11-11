package com.store.core.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.store.core.data.database.customer.CartItemEntity
import com.store.core.data.database.customer.CustomerDao
import com.store.core.data.database.customer.CustomerEntity

internal const val DATABASE_NAME = "store.db"

@Database(
    entities = [CustomerEntity::class, CartItemEntity::class],
    version = 1,
)
@ConstructedBy(StoreDatabaseConstructor::class)
abstract class StoreDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
}

@Suppress("KotlinNoActualForExpect")
expect object StoreDatabaseConstructor : RoomDatabaseConstructor<StoreDatabase> {
    override fun initialize(): StoreDatabase
}
