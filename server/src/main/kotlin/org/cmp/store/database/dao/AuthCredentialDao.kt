package org.cmp.store.database.dao

import org.cmp.store.database.tables.AuthCredentialTable
import org.cmp.store.domain.auth.AuthCredential
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.utils.dbQuery
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

interface AuthCredentialDao {
    suspend fun findByProviderAndProviderUserId(
        provider: AuthProvider,
        providerUserId: String,
    ): AuthCredential?
    suspend fun findByProviderAndEmail(
        provider: AuthProvider,
        email: String,
    ): AuthCredential?
    suspend fun create(credential: AuthCredential): AuthCredential
    /**
     * Inserts a credential using the caller's existing transaction.
     * Use this to compose a credential insert atomically with other writes.
     */
    fun insertWithinTransaction(credential: AuthCredential)
}

class AuthCredentialDaoImpl : AuthCredentialDao {

    override suspend fun findByProviderAndProviderUserId(
        provider: AuthProvider,
        providerUserId: String,
    ): AuthCredential? = dbQuery {
        AuthCredentialTable
            .selectAll()
            .where {
                (AuthCredentialTable.provider eq provider.name) and
                        (AuthCredentialTable.providerUserId eq providerUserId)
            }
            .singleOrNull()
            ?.toAuthCredential()
    }

    override suspend fun findByProviderAndEmail(
        provider: AuthProvider,
        email: String,
    ): AuthCredential? = dbQuery {
        AuthCredentialTable
            .selectAll()
            .where {
                (AuthCredentialTable.provider eq provider.name) and
                        (AuthCredentialTable.email eq email)
            }
            .singleOrNull()
            ?.toAuthCredential()
    }

    override suspend fun create(credential: AuthCredential): AuthCredential = dbQuery {
        AuthCredentialTable.insert { it.mapFrom(credential) }
        credential
    }

    override fun insertWithinTransaction(credential: AuthCredential) {
        AuthCredentialTable.insert { it.mapFrom(credential) }
    }

    private fun ResultRow.toAuthCredential(): AuthCredential = AuthCredential(
        id = this[AuthCredentialTable.id],
        customerId = this[AuthCredentialTable.customerId],
        provider = AuthProvider.valueOf(this[AuthCredentialTable.provider]),
        providerUserId = this[AuthCredentialTable.providerUserId],
        email = this[AuthCredentialTable.email],
        passwordHash = this[AuthCredentialTable.passwordHash],
        isVerified = this[AuthCredentialTable.isVerified],
        createdAt = this[AuthCredentialTable.createdAt]
    )

    private fun UpdateBuilder<*>.mapFrom(credential: AuthCredential) {
        this[AuthCredentialTable.id] = credential.id
        this[AuthCredentialTable.customerId] = credential.customerId
        this[AuthCredentialTable.provider] = credential.provider.name
        this[AuthCredentialTable.providerUserId] = credential.providerUserId
        this[AuthCredentialTable.email] = credential.email
        this[AuthCredentialTable.passwordHash] = credential.passwordHash
        this[AuthCredentialTable.isVerified] = credential.isVerified
        this[AuthCredentialTable.createdAt] = credential.createdAt
    }
}
