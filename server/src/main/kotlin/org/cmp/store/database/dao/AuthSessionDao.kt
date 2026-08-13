package org.cmp.store.database.dao

import org.cmp.store.database.tables.AuthSessionTable
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.features.session.dto.AuthSessionDto
import org.cmp.store.utils.dbQuery
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

interface AuthSessionDao {
    suspend fun issue(token: String, session: AuthSessionDto)
    suspend fun find(token: String): AuthSessionDto?
    suspend fun delete(token: String)
    fun issueWithinTransaction(token: String, session: AuthSessionDto)
    fun deleteByFamilyWithinTransaction(familyId: String)
    suspend fun deleteExpiredBefore(timestamp: Long): Int
}

class AuthSessionDaoImpl : AuthSessionDao {
    override suspend fun issue(token: String, session: AuthSessionDto): Unit = dbQuery {
        issueWithinTransaction(token, session)
    }

    override fun issueWithinTransaction(token: String, session: AuthSessionDto) {
        AuthSessionTable.insert {
            it[this.token] = token
            it[customerId] = session.customerId
            it[provider] = session.provider.name
            it[expiresAt] = session.expiresAt
            it[familyId] = session.familyId
        }
    }

    override suspend fun find(token: String): AuthSessionDto? = dbQuery {
        AuthSessionTable
            .selectAll()
            .where { AuthSessionTable.token eq token }
            .singleOrNull()
            ?.toAuthSession()
    }

    override suspend fun delete(token: String): Unit = dbQuery {
        AuthSessionTable.deleteWhere { this.token eq token }
    }

    /**
     * Revoking a login chain has to drop its access tokens too — they outlive the refresh
     * token that minted them and would otherwise keep working until their own expiry.
     */
    override fun deleteByFamilyWithinTransaction(familyId: String) {
        AuthSessionTable.deleteWhere { AuthSessionTable.familyId eq familyId }
    }

    override suspend fun deleteExpiredBefore(timestamp: Long): Int = dbQuery {
        AuthSessionTable.deleteWhere { expiresAt less timestamp }
    }

    private fun ResultRow.toAuthSession(): AuthSessionDto = AuthSessionDto(
        customerId = this[AuthSessionTable.customerId],
        provider = AuthProvider.valueOf(this[AuthSessionTable.provider]),
        expiresAt = this[AuthSessionTable.expiresAt],
        familyId = this[AuthSessionTable.familyId],
    )
}
