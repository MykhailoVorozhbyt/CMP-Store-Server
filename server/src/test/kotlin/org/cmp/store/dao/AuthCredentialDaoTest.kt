package org.cmp.store.dao

import org.cmp.store.database.dao.AuthCredentialDaoImpl
import org.cmp.store.database.dao.CustomerDaoImpl
import org.cmp.store.domain.auth.AuthCredential
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.utils.customerFixture
import org.cmp.store.utils.testDaoDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthCredentialDaoTest {

    private val customerDao = CustomerDaoImpl()
    private val authCredentialDao = AuthCredentialDaoImpl()

    @Test
    fun create_persists_all_fields() = testDaoDatabase {
        val customer = customerFixture(id = "credential-create")
        customerDao.create(customer)
        val credential = authCredentialFixture(
            id = "credential-1",
            customerId = customer.id,
            provider = AuthProvider.GOOGLE,
            providerUserId = "google-user-1",
            email = customer.email,
            passwordHash = "hash-value",
            isVerified = true,
            createdAt = 123456789L,
        )

        authCredentialDao.create(credential)

        val stored = assertNotNull(
            authCredentialDao.findByProviderAndEmail(AuthProvider.GOOGLE, customer.email)
        )
        assertEquals(credential, stored)
    }

    @Test
    fun find_by_provider_and_email_works() = testDaoDatabase {
        val customer = customerFixture(id = "credential-email")
        customerDao.create(customer)
        val credential = authCredentialFixture(
            id = "credential-email-1",
            customerId = customer.id,
            provider = AuthProvider.MANUAL,
            email = customer.email,
        )
        authCredentialDao.create(credential)

        val found = authCredentialDao.findByProviderAndEmail(AuthProvider.MANUAL, customer.email)

        assertEquals(credential, found)
    }

    @Test
    fun find_by_provider_and_providerUserId_works() = testDaoDatabase {
        val customer = customerFixture(id = "credential-provider-id")
        customerDao.create(customer)
        val credential = authCredentialFixture(
            id = "credential-provider-id-1",
            customerId = customer.id,
            provider = AuthProvider.GOOGLE,
            providerUserId = "google-provider-id",
            email = customer.email,
        )
        authCredentialDao.create(credential)

        val found = authCredentialDao.findByProviderAndProviderUserId(
            AuthProvider.GOOGLE,
            "google-provider-id"
        )

        assertEquals(credential, found)
    }

    @Test
    fun provider_enum_round_trip_works() = testDaoDatabase {
        val customer = customerFixture(id = "credential-provider-roundtrip")
        customerDao.create(customer)
        val credential = authCredentialFixture(
            id = "credential-provider-roundtrip-1",
            customerId = customer.id,
            provider = AuthProvider.FACEBOOK,
            providerUserId = "facebook-user-1",
            email = customer.email,
        )
        authCredentialDao.create(credential)

        val found = assertNotNull(
            authCredentialDao.findByProviderAndEmail(AuthProvider.FACEBOOK, customer.email)
        )
        assertEquals(AuthProvider.FACEBOOK, found.provider)
    }

    private fun authCredentialFixture(
        id: String,
        customerId: String,
        provider: AuthProvider,
        email: String,
        providerUserId: String? = null,
        passwordHash: String? = null,
        isVerified: Boolean = false,
        createdAt: Long = 1_717_000_000_000,
    ) = AuthCredential(
        id = id,
        customerId = customerId,
        provider = provider,
        providerUserId = providerUserId,
        email = email,
        passwordHash = passwordHash,
        isVerified = isVerified,
        createdAt = createdAt,
    )
}

