package org.cmp.store.utils

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.cmp.store.database.DatabaseFactory
import org.cmp.store.database.dao.CustomerDaoImpl
import org.cmp.store.database.tables.CustomerTable
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber
import org.cmp.store.features.customer.dto.CartItemDto
import org.cmp.store.features.customer.dto.CustomerDto
import org.cmp.store.features.customer.dto.PhoneNumberDto
import org.cmp.store.module
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.update
import org.koin.ktor.plugin.KoinIsolated
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import kotlin.test.fail

internal val testJson = Json {
    ignoreUnknownKeys = true
}

internal fun testServerApplication(
    block: suspend ApplicationTestBuilder.() -> Unit,
) = testApplication {
    val databaseConfig = isolatedDatabaseConfig(prefix = "server-route")

    environment {
        config = MapApplicationConfig(
            DatabaseFactory.URL_KEY to databaseConfig.url,
            DatabaseFactory.DRIVER_KEY to databaseConfig.driver,
            DatabaseFactory.RECREATE_SCHEMA_KEY to databaseConfig.recreateSchema.toString(),
            "jwt.realm" to "CMP Store Test",
        )
    }

    application {
        module(koinPlugin = KoinIsolated)
    }

    // Force the application (and thus DatabaseFactory.init/Database.connect) to start
    // before the test body runs. testApplication starts the app lazily on the first
    // client request, but tests seed the DB before that — without this, seeding hits
    // no/other default database.
    startApplication()

    block()
}

internal fun testDaoDatabase(
    block: suspend () -> Unit,
) = runTest {
    DatabaseFactory.init(isolatedDatabaseConfig(prefix = "server-dao"))
    block()
}

private fun isolatedDatabaseConfig(prefix: String): DatabaseFactory.Config {
    val dbDir = Path.of("build", "test-db")
    Files.createDirectories(dbDir)
    val dbFile = dbDir.resolve("$prefix-${UUID.randomUUID()}.sqlite")

    return DatabaseFactory.Config(
        url = "jdbc:sqlite:${dbFile.toString().replace('\\', '/')}",
        driver = DatabaseFactory.SQLITE_DRIVER,
        recreateSchema = true,
    )
}

/**
 * Seeds a customer straight through the DAO. There is no HTTP route that creates a customer
 * (creation is owned by AuthService during /auth/authorize), so route tests that need an
 * existing customer write one directly against the same isolated per-test database the
 * running application is connected to — the same approach the tests already use to mint
 * session tokens.
 */
internal suspend fun seedCustomer(customer: Customer): Customer {
    CustomerDaoImpl().create(customer)
    return customer
}

/**
 * Grants the admin role the only way it can be granted today: straight at the column,
 * bypassing [CustomerDaoImpl] entirely. The DAO deliberately never writes `is_admin`, so
 * seeding an admin through it is impossible — which is exactly the property under test.
 */
internal suspend fun grantAdmin(customerId: String): Unit = dbQuery {
    CustomerTable.update({ CustomerTable.id eq customerId }) {
        it[isAdmin] = true
    }
}

internal suspend inline fun <reified T> HttpResponse.decodeJson(): T =
    testJson.decodeFromString(bodyAsText())

internal suspend inline fun <reified T : Throwable> assertFailsWithSuspend(
    noinline block: suspend () -> Unit,
): T {
    try {
        block()
    } catch (throwable: Throwable) {
        if (throwable is T) return throwable
        throw AssertionError(
            "Expected ${T::class.simpleName}, but was ${throwable::class.simpleName}",
            throwable
        )
    }
    fail("Expected ${T::class.simpleName} to be thrown")
}

internal inline fun <reified T> T.toJson(): String = testJson.encodeToString(this)

internal fun customerFixture(
    id: String,
    email: String = "$id@example.com",
    firstName: String = "First$id",
    lastName: String = "Last$id",
    city: String? = "Kyiv",
    postalCode: Int? = 12345,
    address: String? = "Main street $id",
    phoneNumber: PhoneNumber? = PhoneNumber(380, "501112233"),
    cart: List<CartItem> = emptyList(),
    isAdmin: Boolean = false,
): Customer = Customer(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    city = city,
    postalCode = postalCode,
    address = address,
    phoneNumber = phoneNumber,
    cart = cart,
    isAdmin = isAdmin,
)

internal fun customerDtoFixture(
    id: String,
    email: String = "$id@example.com",
    firstName: String = "First$id",
    lastName: String = "Last$id",
    city: String? = "Kyiv",
    postalCode: Int? = 12345,
    address: String? = "Main street $id",
    phoneNumber: PhoneNumberDto? = PhoneNumberDto(380, "501112233"),
    cart: List<CartItemDto> = emptyList(),
    isAdmin: Boolean = false,
): CustomerDto = CustomerDto(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    city = city,
    postalCode = postalCode,
    address = address,
    phoneNumber = phoneNumber,
    cart = cart,
    isAdmin = isAdmin,
)
