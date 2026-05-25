package org.cmp.store.database.tables

import org.jetbrains.exposed.v1.core.Table

/**
 * @property provider MANUAL | GOOGLE | FACEBOOK
 * @property providerUserId google/facebook provider user id
 * null for manual email+password
 * @property email required for all providers
 * @property passwordHash filled only for provider = MANUAL
 * @property isVerified for manual login -> after email verification
 * for google/facebook -> true if provider returned verified email
 * */
object AuthCredentialTable : Table("auth_credentials") {
    val id = varchar("id", 128)
    val customerId = varchar("customer_id", 128).references(CustomerTable.id)
    val provider = varchar("provider", 32)
    val providerUserId = varchar("provider_user_id", 255).nullable()
    val email = varchar("email", 255)
    val passwordHash = varchar("password_hash", 512).nullable()
    val isVerified = bool("is_verified").default(false)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(provider, email)
        uniqueIndex(provider, providerUserId)
    }
}
