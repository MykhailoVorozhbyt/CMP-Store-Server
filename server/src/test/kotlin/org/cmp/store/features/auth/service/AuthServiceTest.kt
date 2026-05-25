package org.cmp.store.features.auth.service

import io.ktor.http.HttpStatusCode
import org.cmp.store.database.dao.AuthCredentialDaoImpl
import org.cmp.store.database.dao.AuthSessionDaoImpl
import org.cmp.store.database.dao.CustomerDaoImpl
import org.cmp.store.database.dao.RefreshTokenDaoImpl
import org.cmp.store.database.tables.AuthCredentialTable
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.features.session.service.AuthSessionServiceImpl
import org.cmp.store.features.session.service.RefreshGraceCache
import org.cmp.store.network.NetworkError
import org.cmp.store.utils.ApiException
import org.cmp.store.utils.RacingCredentialDao
import org.cmp.store.utils.RacingCustomerDao
import org.cmp.store.utils.assertFailsWithSuspend
import org.cmp.store.utils.customerFixture
import org.cmp.store.utils.testDaoDatabase
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthServiceTest {

    private val customerDao = CustomerDaoImpl()
    private val authCredentialDao = AuthCredentialDaoImpl()
    private val authSessionService =
        AuthSessionServiceImpl(AuthSessionDaoImpl(), RefreshTokenDaoImpl(), RefreshGraceCache())
    private val authService = AuthServiceImpl(customerDao, authCredentialDao, authSessionService)

    @Test
    fun losing_the_signup_race_returns_conflict() = testDaoDatabase {
        val racingService = AuthServiceImpl(
            customerDao = RacingCustomerDao(customerDao),
            authCredentialDao = authCredentialDao,
            authSessionService = authSessionService,
        )
        val request = AuthRequest(
            provider = AuthProvider.MANUAL,
            email = "manual-race@example.com",
            password = "secret123",
        )
        val error = assertFailsWithSuspend<ApiException> {
            racingService.authorize(request)
        }
        // The caller must see a plain 409, not the raw SQL failure leaking out as a 500.
        assertEquals(HttpStatusCode.Conflict, error.statusCode)
        assertEquals(NetworkError.USER_ALREADY_EXISTS, error.networkError)
        // Neither row appears. Note this does not yet prove a rollback: the failure hits the
        // first insert, so nothing was ever written to undo. See
        // [losing_the_signup_race_rolls_back_the_customer_insert] for that half.
        assertNull(customerDao.findByEmail(request.email))
        assertNull(authCredentialDao.findByProviderAndEmail(AuthProvider.MANUAL, request.email))
    }

    /**
     * The rollback half: the customer insert succeeds and only the credential insert fails,
     * so a row really does exist inside the open transaction. If `persistNewManualAccount`
     * ever splits its two writes into separate `dbQuery` calls, that customer survives as an
     * orphan with no credential — an account nobody can ever log into, holding its email
     * hostage against the unique constraint. This test is what catches that.
     */
    @Test
    fun losing_the_signup_race_rolls_back_the_customer_insert() = testDaoDatabase {
        val racingService = AuthServiceImpl(
            customerDao = customerDao,
            authCredentialDao = RacingCredentialDao(authCredentialDao),
            authSessionService = authSessionService,
        )
        val request = AuthRequest(
            provider = AuthProvider.MANUAL,
            email = "manual-race-rollback@example.com",
            password = "secret123",
        )

        val error = assertFailsWithSuspend<ApiException> {
            racingService.authorize(request)
        }

        assertEquals(HttpStatusCode.Conflict, error.statusCode)
        assertEquals(NetworkError.USER_ALREADY_EXISTS, error.networkError)
        assertNull(customerDao.findByEmail(request.email))
        assertNull(authCredentialDao.findByProviderAndEmail(AuthProvider.MANUAL, request.email))
    }

    @Test
    fun new_manual_user_creates_customer_and_credential() = testDaoDatabase {
        val request = AuthRequest(
            provider = AuthProvider.MANUAL,
            email = "manual-new@example.com",
            password = "secret123",
            firstName = "Manual",
            lastName = "New",
        )

        val response = authService.authorize(request)
        val credential = assertNotNull(
            authCredentialDao.findByProviderAndEmail(AuthProvider.MANUAL, request.email)
        )

        assertTrue(response.isNewAccount)
        assertEquals(AuthProvider.MANUAL, response.provider)
        assertEquals(request.email, response.customer.email)
        assertEquals("Manual", response.customer.firstName)
        assertEquals("New", response.customer.lastName)
        assertEquals(response.customer.id, credential.customerId)
        assertTrue(PasswordHasher.verify("secret123", credential.passwordHash.orEmpty()))
    }

    @Test
    fun existing_manual_user_with_correct_password_logs_in() = testDaoDatabase {
        val request = AuthRequest(
            provider = AuthProvider.MANUAL,
            email = "manual-existing@example.com",
            password = "secret123",
        )

        val first = authService.authorize(request)
        val second = authService.authorize(request)

        assertTrue(first.isNewAccount)
        assertFalse(second.isNewAccount)
        assertEquals(first.customer.id, second.customer.id)
        assertNotEquals(first.accessToken, second.accessToken)
    }

    @Test
    fun existing_manual_user_with_wrong_password_fails() = testDaoDatabase {
        authService.authorize(
            AuthRequest(
                provider = AuthProvider.MANUAL,
                email = "manual-wrong@example.com",
                password = "secret123",
            )
        )

        val error = assertFailsWithSuspend<ApiException> {
            authService.authorize(
                AuthRequest(
                    provider = AuthProvider.MANUAL,
                    email = "manual-wrong@example.com",
                    password = "wrong-password",
                )
            )
        }

        assertEquals(HttpStatusCode.Unauthorized, error.statusCode)
        assertEquals(NetworkError.INVALID_CREDENTIALS, error.networkError)
    }

    @Test
    fun manual_auth_on_email_owned_by_another_account_is_rejected() = testDaoDatabase {
        // An account already owns this email (e.g. registered via Google, no password).
        val existingCustomer = customerFixture(
            id = "existing-customer",
            email = "reuse-manual@example.com",
            firstName = "Existing",
            lastName = "Customer",
        )
        customerDao.create(existingCustomer)

        val error = assertFailsWithSuspend<ApiException> {
            authService.authorize(
                AuthRequest(
                    provider = AuthProvider.MANUAL,
                    email = existingCustomer.email,
                    password = "secret123",
                )
            )
        }

        // The manual password must NOT be attached to the existing account (account takeover).
        assertEquals(HttpStatusCode.Conflict, error.statusCode)
        assertEquals(NetworkError.ACCOUNT_HAS_NO_PASSWORD, error.networkError)
        assertNull(
            authCredentialDao.findByProviderAndEmail(AuthProvider.MANUAL, existingCustomer.email)
        )
    }

    @Test
    fun new_google_and_facebook_user_creates_customer_and_social_credential() = testDaoDatabase {
        listOf(AuthProvider.GOOGLE, AuthProvider.FACEBOOK).forEach { provider ->
            val response = authService.authorize(
                AuthRequest(
                    provider = provider,
                    email = "${provider.name.lowercase()}-new@example.com",
                    providerUserId = "${provider.name.lowercase()}-uid-1",
                    displayName = "${provider.name.lowercase()} user",
                )
            )

            val credential = assertNotNull(
                authCredentialDao.findByProviderAndEmail(provider, response.customer.email)
            )
            assertTrue(response.isNewAccount)
            assertEquals(provider, response.provider)
            assertEquals(response.customer.id, credential.customerId)
            assertEquals("${provider.name.lowercase()}-uid-1", credential.providerUserId)
        }
    }

    @Test
    fun existing_social_credential_logs_in_existing_account() = testDaoDatabase {
        val request = AuthRequest(
            provider = AuthProvider.GOOGLE,
            email = "social-existing@example.com",
            providerUserId = "google-uid-1",
            displayName = "Social Existing",
        )

        val first = authService.authorize(request)
        val second = authService.authorize(request)

        assertTrue(first.isNewAccount)
        assertFalse(second.isNewAccount)
        assertEquals(first.customer.id, second.customer.id)
    }

    @Test
    fun existing_customer_by_same_email_gets_linked_social_credential() = testDaoDatabase {
        val existingCustomer = customerFixture(
            id = "social-link-customer",
            email = "social-link@example.com",
        )
        customerDao.create(existingCustomer)

        val response = authService.authorize(
            AuthRequest(
                provider = AuthProvider.GOOGLE,
                email = existingCustomer.email,
                providerUserId = "google-link-uid",
                displayName = "Social Link",
            )
        )

        val credential = assertNotNull(
            authCredentialDao.findByProviderAndEmail(AuthProvider.GOOGLE, existingCustomer.email)
        )
        assertEquals(existingCustomer.id, response.customer.id)
        assertFalse(response.isNewAccount)
        assertEquals(existingCustomer.id, credential.customerId)
    }

    @Test
    fun social_credential_mismatch_throws_invalid_credentials() = testDaoDatabase {
        authService.authorize(
            AuthRequest(
                provider = AuthProvider.GOOGLE,
                email = "social-mismatch@example.com",
                providerUserId = "google-uid-1",
                displayName = "Social Mismatch",
            )
        )

        val error = assertFailsWithSuspend<ApiException> {
            authService.authorize(
                AuthRequest(
                    provider = AuthProvider.GOOGLE,
                    email = "social-mismatch@example.com",
                    providerUserId = "google-uid-2",
                    displayName = "Social Mismatch",
                )
            )
        }

        assertEquals(HttpStatusCode.Unauthorized, error.statusCode)
        assertEquals(NetworkError.INVALID_CREDENTIALS, error.networkError)
    }

    @Test
    fun display_name_parsing_produces_first_and_last_names_correctly() = testDaoDatabase {
        val response = authService.authorize(
            AuthRequest(
                provider = AuthProvider.GOOGLE,
                email = "display-name@example.com",
                providerUserId = "google-display-uid",
                displayName = "John Ronald Reuel",
            )
        )

        assertEquals("John", response.customer.firstName)
        assertEquals("Ronald Reuel", response.customer.lastName)
    }

    @Test
    fun missing_customer_for_stored_credential_throws_customer_not_found() = testDaoDatabase {
        transaction {
            AuthCredentialTable.insert {
                it[id] = "dangling-credential"
                it[customerId] = "missing-customer-id"
                it[provider] = AuthProvider.GOOGLE.name
                it[providerUserId] = "google-dangling-uid"
                it[email] = "dangling@example.com"
                it[passwordHash] = null
                it[isVerified] = false
                it[createdAt] = 1_717_000_000_000
            }
        }

        val error = assertFailsWithSuspend<ApiException> {
            authService.authorize(
                AuthRequest(
                    provider = AuthProvider.GOOGLE,
                    email = "dangling@example.com",
                    providerUserId = "google-dangling-uid",
                    displayName = "Dangling User",
                )
            )
        }

        assertEquals(HttpStatusCode.NotFound, error.statusCode)
        assertEquals(NetworkError.CUSTOMER_NOT_FOUND, error.networkError)
    }
}
