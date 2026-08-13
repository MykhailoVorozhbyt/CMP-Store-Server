package org.cmp.store.features

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.cmp.store.database.dao.AuthSessionDaoImpl
import org.cmp.store.database.dao.CustomerDaoImpl
import org.cmp.store.database.dao.RefreshTokenDaoImpl
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.PhoneNumber
import org.cmp.store.features.session.service.AuthSessionServiceImpl
import org.cmp.store.features.session.service.RefreshGraceCache
import org.cmp.store.features.customer.dto.CartItemDto
import org.cmp.store.features.customer.dto.CustomerDto
import org.cmp.store.features.customer.dto.PhoneNumberDto
import org.cmp.store.features.customer.mappers.toDto
import org.cmp.store.network.NetworkError
import org.cmp.store.utils.customerDtoFixture
import org.cmp.store.utils.customerFixture
import org.cmp.store.utils.decodeJson
import org.cmp.store.utils.grantAdmin
import org.cmp.store.utils.seedCustomer
import org.cmp.store.utils.testServerApplication
import org.cmp.store.utils.toJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CustomerRoutesIntegrationTest {

    /**
     * Customers are only ever created by AuthService during `/auth/authorize`, so these tests
     * mint a session token directly against the same (isolated, per-test) database the running
     * application is connected to, the same way [org.cmp.store.utils.seedCustomer] writes the
     * customer itself.
     */
    private suspend fun bearerTokenFor(customerId: String): String =
        AuthSessionServiceImpl(AuthSessionDaoImpl(), RefreshTokenDaoImpl(), RefreshGraceCache())
            .issueSession(customerId, AuthProvider.MANUAL)
            .accessToken

    @Test
    fun get_customer_by_id_returns_customer() = testServerApplication {
        val customer = seedCustomer(customerFixture(id = "customer-get"))
        val token = bearerTokenFor(customer.id)

        val response = client.get("/customer/${customer.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(customer.toDto(), response.decodeJson<CustomerDto>())
    }

    @Test
    fun get_customer_without_token_returns_401() = testServerApplication {
        val response = client.get("/customer/missing-customer")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun get_customer_with_another_customers_token_returns_403() = testServerApplication {
        val customer = seedCustomer(customerFixture(id = "customer-owned-by-another"))
        val otherCustomersToken = bearerTokenFor("someone-else")

        val response = client.get("/customer/${customer.id}") {
            header(HttpHeaders.Authorization, "Bearer $otherCustomersToken")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals(NetworkError.FORBIDDEN.name, response.bodyAsText())
    }

    @Test
    fun get_customer_by_id_for_missing_customer_returns_404() = testServerApplication {
        val token = bearerTokenFor("missing-customer")

        val response = client.get("/customer/missing-customer") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(NetworkError.CUSTOMER_NOT_FOUND.name, response.bodyAsText())
    }

    @Test
    fun put_customer_updates_customer_and_nested_relations() = testServerApplication {
        val created = seedCustomer(
            customerFixture(
                id = "customer-update",
                phoneNumber = PhoneNumber(380, "501112233"),
                cart = listOf(CartItem(id = "cart-old", productId = "product-1", quantity = 1)),
            )
        )
        val token = bearerTokenFor(created.id)

        val updated = created.toDto().copy(
            firstName = "Updated",
            city = "Lviv",
            phoneNumber = PhoneNumberDto(1, "999888777"),
            cart = listOf(
                CartItemDto(
                    id = "cart-new-1",
                    productId = "product-2",
                    flavor = "Chocolate",
                    quantity = 3
                ),
                CartItemDto(id = "cart-new-2", productId = "product-3", quantity = 2),
            ),
        )

        val putResponse = client.put("/customer") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(updated.toJson())
        }
        val getResponse = client.get("/customer/${updated.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, putResponse.status)
        assertEquals(updated, putResponse.decodeJson<CustomerDto>())
        assertEquals(updated, getResponse.decodeJson<CustomerDto>())
    }

    @Test
    fun put_customer_with_another_customers_token_returns_403() = testServerApplication {
        val created = seedCustomer(customerFixture(id = "customer-update-guarded"))
        val otherCustomersToken = bearerTokenFor("someone-else")

        val response = client.put("/customer") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $otherCustomersToken")
            setBody(created.toDto().copy(firstName = "Hijacked").toJson())
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertEquals(NetworkError.FORBIDDEN.name, response.bodyAsText())
    }

    @Test
    fun put_customer_for_unknown_customer_returns_404() = testServerApplication {
        val missing = customerDtoFixture(id = "customer-missing")
        val token = bearerTokenFor(missing.id)

        val response = client.put("/customer") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(missing.toJson())
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(NetworkError.CUSTOMER_NOT_FOUND.name, response.bodyAsText())
    }

    /**
     * Mass-assignment guard: `isAdmin` ships in CustomerDto because the client renders the
     * admin drawer from it, so a caller can trivially put `isAdmin: true` in the request body.
     * The update path must ignore it — both in the database and in the echoed response.
     */
    @Test
    fun put_customer_cannot_grant_itself_admin() = testServerApplication {
        val created = seedCustomer(customerFixture(id = "customer-privesc", isAdmin = false))
        val token = bearerTokenFor(created.id)

        val response = client.put("/customer") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(created.toDto().copy(isAdmin = true).toJson())
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(false, response.decodeJson<CustomerDto>().isAdmin)
        assertEquals(false, assertNotNull(CustomerDaoImpl().read(created.id)).isAdmin)
    }

    /**
     * The mirror of [put_customer_cannot_grant_itself_admin]: a role granted out of band
     * (admin panel, or by hand in the DB) must survive an ordinary profile edit, which is
     * why `mapFrom` omits the column instead of pinning it to `false`.
     */
    @Test
    fun put_customer_preserves_existing_admin_flag() = testServerApplication {
        val created = seedCustomer(customerFixture(id = "customer-admin"))
        grantAdmin(created.id)
        val token = bearerTokenFor(created.id)

        val response = client.put("/customer") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(created.toDto().copy(firstName = "Renamed", isAdmin = false).toJson())
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(true, response.decodeJson<CustomerDto>().isAdmin)
        assertEquals(true, assertNotNull(CustomerDaoImpl().read(created.id)).isAdmin)
    }
}
