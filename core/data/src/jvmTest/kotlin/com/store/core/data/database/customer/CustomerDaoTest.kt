package com.store.core.data.database.customer

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.store.core.data.database.StoreDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CustomerDaoTest {

    private val database = Room.inMemoryDatabaseBuilder<StoreDatabase>()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
    private val dao = database.customerDao()

    @AfterTest
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun replace_storesTheCustomerWithItsCart() = runTest {
        dao.replace(customer("uid-1"), listOf(cartItem("item-1", "uid-1")))

        val stored = dao.getCustomer()

        assertEquals(customer("uid-1"), stored?.customer)
        assertEquals(listOf(cartItem("item-1", "uid-1")), stored?.cart)
    }

    @Test
    fun replace_dropsThePreviousCustomerAndCart() = runTest {
        dao.replace(customer("uid-1"), listOf(cartItem("item-1", "uid-1")))

        dao.replace(customer("uid-2"), emptyList())

        val stored = dao.getCustomer()
        assertEquals("uid-2", stored?.customer?.id)
        assertEquals(emptyList(), stored?.cart)
    }

    @Test
    fun clear_removesEverything() = runTest {
        dao.replace(customer("uid-1"), listOf(cartItem("item-1", "uid-1")))

        dao.clear()

        assertNull(dao.getCustomer())
        assertNull(dao.observeCustomer().first())
    }

    private fun customer(id: String) = CustomerEntity(
        id = id,
        firstName = "First",
        lastName = "Last",
        email = "user@example.com",
        city = "Kyiv",
        postalCode = 1000,
        address = "Khreshchatyk 1",
        phoneDialCode = 380,
        phoneNumber = "123456789",
        isAdmin = false,
    )

    private fun cartItem(id: String, customerId: String) = CartItemEntity(
        id = id,
        customerId = customerId,
        productId = "product-1",
        flavor = null,
        quantity = 2,
    )
}
