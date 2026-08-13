package org.cmp.store.database.tables

import org.jetbrains.exposed.v1.core.Table

/**
 * @property token opaque bearer token handed to the client; primary key
 * @property provider MANUAL | GOOGLE | FACEBOOK — the provider used to establish this session
 * @property expiresAt epoch millis after which the session is no longer valid
 * @property familyId login chain this token belongs to; lets a whole session be dropped at once
 *   without joining against [RefreshTokenTable]
 * */
object AuthSessionTable : Table("auth_sessions") {
    val token = varchar("token", 128)
    val customerId = varchar("customer_id", 128).references(CustomerTable.id).index()
    val provider = varchar("provider", 32)
    val expiresAt = long("expires_at")
    val familyId = varchar("family_id", 128).index()

    override val primaryKey = PrimaryKey(token)
}
