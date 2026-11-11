package com.store.core.data.database.customer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CustomerDao {

    @Transaction
    @Query("SELECT * FROM customer LIMIT 1")
    abstract fun observeCustomer(): Flow<CustomerWithCart?>

    @Transaction
    @Query("SELECT * FROM customer LIMIT 1")
    abstract suspend fun getCustomer(): CustomerWithCart?

    @Transaction
    open suspend fun replace(customer: CustomerEntity, cart: List<CartItemEntity>) {
        deleteCart()
        deleteCustomers()
        insertCustomer(customer)
        insertCart(cart)
    }

    @Transaction
    open suspend fun clear() {
        deleteCart()
        deleteCustomers()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertCustomer(customer: CustomerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertCart(cart: List<CartItemEntity>)

    @Query("DELETE FROM customer")
    protected abstract suspend fun deleteCustomers()

    @Query("DELETE FROM cart_item")
    protected abstract suspend fun deleteCart()
}
