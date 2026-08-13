package org.cmp.store.features.auth.service

import io.ktor.http.HttpStatusCode
import org.cmp.store.database.dao.AuthCredentialDao
import org.cmp.store.database.dao.CustomerDao
import org.cmp.store.domain.auth.AuthCredential
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse
import org.cmp.store.domain.customer.Customer
import org.cmp.store.features.session.service.AuthSessionService
import org.cmp.store.network.NetworkError
import org.cmp.store.utils.ApiException
import org.cmp.store.utils.dbQuery
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import java.util.UUID

interface AuthService {
    suspend fun authorize(request: AuthRequest): AuthResponse
}

class AuthServiceImpl(
    private val customerDao: CustomerDao,
    private val authCredentialDao: AuthCredentialDao,
    private val authSessionService: AuthSessionService,
) : AuthService {

    override suspend fun authorize(request: AuthRequest): AuthResponse {
        val normalizedEmail = request.email.trim().lowercase()
        requireEmail(normalizedEmail)

        return when (request.provider) {
            AuthProvider.MANUAL -> authorizeManual(request, normalizedEmail)
            AuthProvider.GOOGLE, AuthProvider.FACEBOOK -> authorizeSocial(request, normalizedEmail)
        }
    }

    private suspend fun authorizeManual(
        request: AuthRequest,
        normalizedEmail: String,
    ): AuthResponse {
        authCredentialDao.findByProviderAndEmail(
            provider = AuthProvider.MANUAL,
            email = normalizedEmail
        )?.let { credential ->
            val storedHash = credential.passwordHash
                ?: throw ApiException(
                    HttpStatusCode.Unauthorized,
                    NetworkError.INVALID_CREDENTIALS
                )
            val password = requirePassword(request)
            if (!PasswordHasher.verify(password = password, storedHash = storedHash)) {
                throw ApiException(
                    HttpStatusCode.Unauthorized,
                    NetworkError.INVALID_CREDENTIALS
                )
            }

            return buildAuthResponse(
                customer = requireCustomer(credential.customerId),
                provider = AuthProvider.MANUAL,
                isNewAccount = false
            )
        }

        /**
         * No MANUAL credential exists for this email. A manual account may only be created
         * when the email is completely free — attaching a password to an email that already
         * belongs to another account (e.g. a Google-registered customer with no password) is
         * account takeover. Reject with ACCOUNT_HAS_NO_PASSWORD so the client tells the user
         * to sign in with Google instead.
         * */
        customerDao.findByEmail(normalizedEmail)?.let {
            throw ApiException(HttpStatusCode.Conflict, NetworkError.ACCOUNT_HAS_NO_PASSWORD)
        }

        val customer = buildNewCustomer(normalizedEmail, request)
        val credential = AuthCredential(
            id = UUID.randomUUID().toString(),
            customerId = customer.id,
            provider = AuthProvider.MANUAL,
            email = normalizedEmail,
            passwordHash = PasswordHasher.hash(requirePassword(request)),
            createdAt = System.currentTimeMillis()
        )

        return try {
            persistNewManualAccount(
                isNewCustomer = true,
                customer = customer,
                credential = credential,
            )
            buildAuthResponse(
                customer = customer,
                provider = AuthProvider.MANUAL,
                isNewAccount = true
            )
        } catch (_: ExposedSQLException) {
            // Lost a race: a concurrent request created this customer/credential first
            // (unique violation on customers.email or (provider, email)).
            throw ApiException(HttpStatusCode.Conflict, NetworkError.USER_ALREADY_EXISTS)
        }
    }

    /**
     * Atomically persists a brand-new manual account. Both writes must commit or roll back
     * together — otherwise a mid-way failure could leave an orphaned customer with no credential.
     */
    private suspend fun persistNewManualAccount(
        isNewCustomer: Boolean,
        customer: Customer,
        credential: AuthCredential,
    ) {
        dbQuery {
            if (isNewCustomer) {
                customerDao.insertWithinTransaction(customer)
            }
            authCredentialDao.insertWithinTransaction(credential)
        }
    }

    private suspend fun authorizeSocial(
        request: AuthRequest,
        normalizedEmail: String,
    ): AuthResponse {
        val providerUserId = requireProviderUserId(request)

        authCredentialDao.findByProviderAndProviderUserId(
            provider = request.provider,
            providerUserId = providerUserId
        )?.let { credential ->
            return buildAuthResponse(
                customer = requireCustomer(credential.customerId),
                provider = request.provider,
                isNewAccount = false
            )
        }

        val existingCustomer = customerDao.findByEmail(normalizedEmail)
        if (existingCustomer != null) {
            ensureSocialCredential(
                customerId = existingCustomer.id,
                provider = request.provider,
                providerUserId = providerUserId,
                email = normalizedEmail
            )
            return buildAuthResponse(
                customer = existingCustomer,
                provider = request.provider,
                isNewAccount = false
            )
        }

        val customer = buildNewCustomer(normalizedEmail, request)
        val credential = AuthCredential(
            id = UUID.randomUUID().toString(),
            customerId = customer.id,
            provider = request.provider,
            providerUserId = providerUserId,
            email = normalizedEmail,
            createdAt = System.currentTimeMillis()
        )

        try {
            persistNewSocialAccount(customer, credential)
        } catch (_: ExposedSQLException) {
            // Lost a race: a concurrent request created this customer/credential first
            // (unique violation on customers.email or (provider, providerUserId)).
            throw ApiException(HttpStatusCode.Conflict, NetworkError.USER_ALREADY_EXISTS)
        }

        return buildAuthResponse(
            customer = customer,
            provider = request.provider,
            isNewAccount = true
        )
    }

    /**
     * Atomically persists a brand-new social account — same rationale as
     * [persistNewManualAccount]: both writes must commit or roll back together.
     */
    private suspend fun persistNewSocialAccount(
        customer: Customer,
        credential: AuthCredential,
    ) {
        dbQuery {
            customerDao.insertWithinTransaction(customer)
            authCredentialDao.insertWithinTransaction(credential)
        }
    }

    private fun buildNewCustomer(
        email: String,
        request: AuthRequest,
    ): Customer {
        val (firstName, lastName) = resolveNames(request, email)
        return Customer(
            id = UUID.randomUUID().toString(),
            firstName = firstName,
            lastName = lastName,
            email = email
        )
    }

    private suspend fun ensureSocialCredential(
        customerId: String,
        provider: AuthProvider,
        providerUserId: String,
        email: String,
    ) {
        val existingCredential = authCredentialDao.findByProviderAndEmail(
            provider = provider,
            email = email
        )
        if (existingCredential == null) {
            try {
                createSocialCredential(
                    customerId = customerId,
                    provider = provider,
                    providerUserId = providerUserId,
                    email = email
                )
            } catch (_: ExposedSQLException) {
                // Lost a race: a concurrent sign-in for this customer/provider created
                // the credential first. The caller can simply retry.
                throw ApiException(HttpStatusCode.Conflict, NetworkError.USER_ALREADY_EXISTS)
            }
            return
        }

        if (existingCredential.customerId != customerId || existingCredential.providerUserId != providerUserId) {
            throw ApiException(
                HttpStatusCode.Unauthorized,
                NetworkError.INVALID_CREDENTIALS
            )
        }
    }

    private suspend fun createSocialCredential(
        customerId: String,
        provider: AuthProvider,
        providerUserId: String,
        email: String,
    ) {
        authCredentialDao.create(
            credential = AuthCredential(
                id = UUID.randomUUID().toString(),
                customerId = customerId,
                provider = provider,
                providerUserId = providerUserId,
                email = email,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    private fun requireEmail(email: String) {
        if (email.isBlank()) {
            throw ApiException(HttpStatusCode.BadRequest, NetworkError.EMAIL_REQUIRED)
        }
    }

    private fun requirePassword(request: AuthRequest): String = request.password
        ?.takeIf { it.isNotBlank() }
        ?: throw ApiException(HttpStatusCode.BadRequest, NetworkError.PASSWORD_REQUIRED)

    private fun requireProviderUserId(request: AuthRequest): String = request.providerUserId
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: throw ApiException(
            HttpStatusCode.BadRequest,
            NetworkError.PROVIDER_USER_ID_REQUIRED
        )

    private suspend fun requireCustomer(customerId: String): Customer = customerDao.read(customerId)
        ?: throw ApiException(HttpStatusCode.NotFound, NetworkError.CUSTOMER_NOT_FOUND)

    private suspend fun buildAuthResponse(
        customer: Customer,
        provider: AuthProvider,
        isNewAccount: Boolean,
    ): AuthResponse {
        val tokens = authSessionService.issueSession(customer.id, provider)
        return AuthResponse(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            customer = customer,
            isNewAccount = isNewAccount,
            provider = provider
        )
    }

    private fun resolveNames(
        request: AuthRequest,
        email: String,
    ): Pair<String, String> {
        val firstName = request.firstName?.trim().orEmpty()
        val lastName = request.lastName?.trim().orEmpty()
        if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
            return firstName.ifEmpty { email.substringBefore("@") } to lastName
        }

        val displayName = request.displayName?.trim().orEmpty()
        if (displayName.isNotEmpty()) {
            val parts = displayName.split(Regex("\\s+"))
            return parts.first().ifEmpty { email.substringBefore("@") } to
                    parts.drop(1).joinToString(" ")
        }

        return email.substringBefore("@") to ""
    }
}
