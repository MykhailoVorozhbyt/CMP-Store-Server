package org.cmp.store.database

import io.ktor.server.config.ApplicationConfig
import org.cmp.store.database.tables.AuthCredentialTable
import org.cmp.store.database.tables.AuthSessionTable
import org.cmp.store.database.tables.CartItemTable
import org.cmp.store.database.tables.CustomerTable
import org.cmp.store.database.tables.PhoneNumberTable
import org.cmp.store.database.tables.ProductTable
import org.cmp.store.database.tables.RefreshTokenTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {
    data class Config(
        val url: String = DEFAULT_URL,
        val driver: String = SQLITE_DRIVER,
        val recreateSchema: Boolean = false,
    )

    fun init(config: Config) {
        Database.connect(
            url = config.url.withBusyTimeout(),
            driver = config.driver
        )
        transaction {
            if (config.recreateSchema) {
                SchemaUtils.drop(
                    AuthSessionTable,
                    RefreshTokenTable,
                    AuthCredentialTable,
                    CartItemTable,
                    PhoneNumberTable,
                    ProductTable,
                    CustomerTable,
                )
            }
            SchemaUtils.create(
                AuthCredentialTable,
                AuthSessionTable,
                RefreshTokenTable,
                CustomerTable,
                PhoneNumberTable,
                CartItemTable,
                ProductTable
            )
        }
    }

    fun from(applicationConfig: ApplicationConfig): Config = Config(
        url = applicationConfig.propertyOrNull(URL_KEY)?.getString() ?: DEFAULT_URL,
        driver = applicationConfig.propertyOrNull(DRIVER_KEY)?.getString() ?: SQLITE_DRIVER,
        recreateSchema = applicationConfig.propertyOrNull(RECREATE_SCHEMA_KEY)
            ?.getString()
            ?.toBooleanStrictOrNull()
            ?: false,
    )

    /**
     * SQLite serialises writers with a single file lock and, by default, fails a blocked
     * write immediately with SQLITE_BUSY instead of waiting. Applied here rather than to
     * [DEFAULT_URL] so a URL from application.conf (and from tests) gets it too.
     */
    private fun String.withBusyTimeout(): String = when {
        !startsWith(SQLITE_URL_PREFIX) -> this
        contains(BUSY_TIMEOUT_KEY) -> this
        else -> "$this${if (contains('?')) '&' else '?'}$BUSY_TIMEOUT_KEY=$BUSY_TIMEOUT_MILLIS"
    }

    const val URL_KEY = "db.url"
    const val DRIVER_KEY = "db.driver"
    const val RECREATE_SCHEMA_KEY = "db.recreateSchema"

    private const val DEFAULT_URL = "jdbc:sqlite:./store.db"
    const val SQLITE_DRIVER = "org.sqlite.JDBC"

    private const val SQLITE_URL_PREFIX = "jdbc:sqlite:"
    private const val BUSY_TIMEOUT_KEY = "busy_timeout"
    private const val BUSY_TIMEOUT_MILLIS = 5_000
}
