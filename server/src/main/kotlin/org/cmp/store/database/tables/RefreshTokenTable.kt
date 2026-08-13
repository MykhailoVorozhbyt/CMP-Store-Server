package org.cmp.store.database.tables

import org.jetbrains.exposed.v1.core.Table

/**
 * Long-lived refresh token. Never presented on ordinary requests — its only purpose is to
 * mint a fresh access token once the short-lived one in [AuthSessionTable] expires.
 *
 * Rows are never updated in place on rotation: the old row is marked revoked and a new one
 * is inserted into the same [familyId]. Keeping the spent rows around is what makes theft
 * detection possible — a second attempt to use an already-rotated token means two parties
 * hold the same secret.
 *
 * @property id internal identifier; the raw token is never stored, only [tokenHash]
 * @property tokenHash SHA-256 of the token handed to the client; unique, looked up on refresh
 * @property familyId shared by every token in one login chain; the unit of revocation
 * @property provider MANUAL | GOOGLE | FACEBOOK — carried so rotation can re-stamp the access token
 * @property expiresAt epoch millis; slides forward on every rotation
 * @property revokedAt epoch millis when this token was spent or invalidated; null = active
 * @property replacedBy id of the token that superseded this one, for tracing a rotation chain
 * */
object RefreshTokenTable : Table("refresh_tokens") {
    val id = varchar("id", 128)
    val tokenHash = varchar("token_hash", 128).uniqueIndex()
    val familyId = varchar("family_id", 128).index()
    val customerId = varchar("customer_id", 128).references(CustomerTable.id).index()
    val provider = varchar("provider", 32)
    val expiresAt = long("expires_at")
    val revokedAt = long("revoked_at").nullable()
    val replacedBy = varchar("replaced_by", 128).nullable()

    override val primaryKey = PrimaryKey(id)
}
