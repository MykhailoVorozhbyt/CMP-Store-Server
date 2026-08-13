package org.cmp.store.dao

import org.cmp.store.database.dao.CustomerDaoImpl
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.PhoneNumber
import org.cmp.store.utils.customerFixture
import org.cmp.store.utils.grantAdmin
import org.cmp.store.utils.testDaoDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CustomerDaoTest {

    private val customerDao = CustomerDaoImpl()

    @Test
    fun create_persists_base_fields() = testDaoDatabase {
        val customer = customerFixture(
            id = "customer-base",
            phoneNumber = null,
            cart = emptyList(),
        )

        customerDao.create(customer)

        val stored = assertNotNull(customerDao.read(customer.id))
        assertEquals(customer.id, stored.id)
        assertEquals(customer.firstName, stored.firstName)
        assertEquals(customer.lastName, stored.lastName)
        assertEquals(customer.email, stored.email)
        assertEquals(customer.city, stored.city)
        assertEquals(customer.postalCode, stored.postalCode)
        assertEquals(customer.address, stored.address)
        assertEquals(customer.isAdmin, stored.isAdmin)
    }

    @Test
    fun create_persists_phone_number() = testDaoDatabase {
        val customer = customerFixture(
            id = "customer-phone",
            phoneNumber = PhoneNumber(48, "123456789"),
            cart = emptyList(),
        )

        customerDao.create(customer)

        val stored = assertNotNull(customerDao.read(customer.id))
        assertEquals(customer.phoneNumber, stored.phoneNumber)
    }

    @Test
    fun create_persists_cart_items() = testDaoDatabase {
        val customer = customerFixture(
            id = "customer-cart",
            phoneNumber = null,
            cart = listOf(
                CartItem(id = "cart-1", productId = "product-1", quantity = 1),
                CartItem(id = "cart-2", productId = "product-2", flavor = "Vanilla", quantity = 3),
            ),
        )

        customerDao.create(customer)

        val stored = assertNotNull(customerDao.read(customer.id))
        assertEquals(customer.cart, stored.cart)
    }

    @Test
    fun read_reconstructs_nested_relations_correctly() = testDaoDatabase {
        val customer = customerFixture(
            id = "customer-read",
            phoneNumber = PhoneNumber(380, "500000001"),
            cart = listOf(
                CartItem(id = "cart-read-1", productId = "product-1", quantity = 2),
                CartItem(id = "cart-read-2", productId = "product-2", flavor = "Chocolate", quantity = 5),
            ),
        )

        customerDao.create(customer)

        val stored = customerDao.read(customer.id)
        assertEquals(customer, stored)
    }

    @Test
    fun update_replaces_phone_and_cart_relations_not_appends() = testDaoDatabase {
        val original = customerFixture(
            id = "customer-update",
            phoneNumber = PhoneNumber(380, "501112233"),
            cart = listOf(
                CartItem(id = "old-cart-1", productId = "product-1", quantity = 1),
                CartItem(id = "old-cart-2", productId = "product-2", quantity = 2),
            ),
        )
        customerDao.create(original)

        val updated = original.copy(
            firstName = "Updated",
            phoneNumber = PhoneNumber(1, "999888777"),
            cart = listOf(
                CartItem(id = "new-cart-1", productId = "product-3", quantity = 4),
            ),
        )

        val result = customerDao.update(updated)
        val stored = customerDao.read(updated.id)

        assertTrue(result)
        assertEquals(updated, stored)
    }

    @Test
    fun exists_works() = testDaoDatabase {
        val customer = customerFixture(id = "customer-exists")

        assertFalse(customerDao.exists(customer.id))
        customerDao.create(customer)
        assertTrue(customerDao.exists(customer.id))
    }

    @Test
    fun findByEmail_works() = testDaoDatabase {
        val customer = customerFixture(id = "customer-email", email = "find-by-email@example.com")
        customerDao.create(customer)

        val found = customerDao.findByEmail("find-by-email@example.com")

        assertEquals(customer, found)
    }

    /**
     * The DAO never writes `is_admin` — not on insert, not on update — so the role can only
     * ever be granted out of band.
     */
    @Test
    fun create_ignores_admin_flag_from_caller() = testDaoDatabase {
        val customer = customerFixture(id = "customer-admin-insert", isAdmin = true)
        customerDao.create(customer)
        assertFalse(assertNotNull(customerDao.read(customer.id)).isAdmin)
    }

    @Test
    fun update_ignores_admin_flag_and_preserves_stored_one() = testDaoDatabase {
        val customer = customerFixture(id = "customer-admin-update")
        customerDao.create(customer)
        grantAdmin(customer.id)

        customerDao.update(customer.copy(firstName = "Renamed", isAdmin = false))

        val stored = assertNotNull(customerDao.read(customer.id))
        assertEquals("Renamed", stored.firstName)
        assertTrue(stored.isAdmin)
    }

    @Test
    fun update_unknown_customer_returns_false() = testDaoDatabase {
        val result = customerDao.update(customerFixture(id = "customer-missing"))

        assertFalse(result)
        assertNull(customerDao.read("customer-missing"))
    }
}

