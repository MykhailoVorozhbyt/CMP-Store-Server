package org.cmp.store.database.dao

import org.cmp.store.database.tables.RefreshTokenTable
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.features.session.dto.RefreshTokenDto
import org.cmp.store.utils.dbQuery
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

interface RefreshTokenDao {
    fun findByTokenHashWithinTransaction(tokenHash: String): RefreshTokenDto?
    fun createWithinTransaction(refreshToken: RefreshTokenDto)
    fun markReplacedWithinTransaction(id: String, replacedBy: String, revokedAt: Long)
    fun revokeFamilyWithinTransaction(familyId: String, revokedAt: Long)
    fun hasActiveTokenInFamilyWithinTransaction(familyId: String): Boolean
    suspend fun findByFamily(familyId: String): List<RefreshTokenDto>
    suspend fun deleteExpiredBefore(timestamp: Long): Int
}

class RefreshTokenDaoImpl : RefreshTokenDao {

    override fun findByTokenHashWithinTransaction(tokenHash: String): RefreshTokenDto? =
        RefreshTokenTable
            .selectAll()
            .where { RefreshTokenTable.tokenHash eq tokenHash }
            .singleOrNull()
            ?.toRefreshToken()

    override fun createWithinTransaction(refreshToken: RefreshTokenDto) {
        RefreshTokenTable.insert {
            it[id] = refreshToken.id
            it[tokenHash] = refreshToken.tokenHash
            it[familyId] = refreshToken.familyId
            it[customerId] = refreshToken.customerId
            it[provider] = refreshToken.provider.name
            it[expiresAt] = refreshToken.expiresAt
            it[revokedAt] = refreshToken.revokedAt
            it[replacedBy] = refreshToken.replacedBy
        }
    }

    override fun markReplacedWithinTransaction(
        id: String,
        replacedBy: String,
        revokedAt: Long,
    ) {
        RefreshTokenTable.update({ RefreshTokenTable.id eq id }) {
            it[RefreshTokenTable.replacedBy] = replacedBy
            it[RefreshTokenTable.revokedAt] = revokedAt
        }
    }

    override fun revokeFamilyWithinTransaction(familyId: String, revokedAt: Long) {
        RefreshTokenTable.update({
            (RefreshTokenTable.familyId eq familyId) and RefreshTokenTable.revokedAt.isNull()
        }) {
            it[RefreshTokenTable.revokedAt] = revokedAt
        }
    }

    /**
     * A login chain is alive while at least one of its tokens is unspent. Rotation always
     * leaves exactly one, so an empty result means the family was torn down — by logout or
     * by theft detection — rather than merely rotated.
     */
    override fun hasActiveTokenInFamilyWithinTransaction(familyId: String): Boolean =
        RefreshTokenTable
            .selectAll()
            .where {
                (RefreshTokenTable.familyId eq familyId) and RefreshTokenTable.revokedAt.isNull()
            }
            .limit(1)
            .any()

    override suspend fun findByFamily(familyId: String): List<RefreshTokenDto> = dbQuery {
        RefreshTokenTable
            .selectAll()
            .where { RefreshTokenTable.familyId eq familyId }
            .map { it.toRefreshToken() }
    }

    /**
     * One rule covers both active and revoked rows: a spent token still has to be
     * recognisable while it would otherwise have been valid, and past that point it is
     * rejected whether the row survives or not.
     */
    override suspend fun deleteExpiredBefore(timestamp: Long): Int = dbQuery {
        RefreshTokenTable.deleteWhere { expiresAt less timestamp }
    }

    private fun ResultRow.toRefreshToken(): RefreshTokenDto = RefreshTokenDto(
        id = this[RefreshTokenTable.id],
        tokenHash = this[RefreshTokenTable.tokenHash],
        familyId = this[RefreshTokenTable.familyId],
        customerId = this[RefreshTokenTable.customerId],
        provider = AuthProvider.valueOf(this[RefreshTokenTable.provider]),
        expiresAt = this[RefreshTokenTable.expiresAt],
        revokedAt = this[RefreshTokenTable.revokedAt],
        replacedBy = this[RefreshTokenTable.replacedBy],
    )
}
