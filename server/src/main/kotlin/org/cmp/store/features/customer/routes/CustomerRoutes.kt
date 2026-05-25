package org.cmp.store.features.customer.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import org.cmp.store.database.dao.CustomerDao
import org.cmp.store.features.auth.models.CustomerPrincipal
import org.cmp.store.features.customer.dto.CustomerDto
import org.cmp.store.features.customer.mappers.toCustomer
import org.cmp.store.features.customer.mappers.toDto
import org.cmp.store.network.NetworkError
import org.cmp.store.plugins.AUTH_TOKEN_PROVIDER
import org.cmp.store.utils.ApiException
import org.cmp.store.utils.Customers
import org.koin.ktor.ext.inject

fun Route.customerRoutes() {
    val customerDao by inject<CustomerDao>()
    authenticate(AUTH_TOKEN_PROVIDER) {
        get<Customers.Id> { resource ->
            requireOwnCustomer(resource.id)
            val customer = customerDao.read(resource.id)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    NetworkError.CUSTOMER_NOT_FOUND.name
                )
            call.respond(customer.toDto())
        }
        put<Customers> {
            val customer = call.receive<CustomerDto>().toCustomer()
            requireOwnCustomer(customer.id)
            if (!customerDao.update(customer)) {
                return@put call.respond(
                    HttpStatusCode.NotFound,
                    NetworkError.CUSTOMER_NOT_FOUND.name
                )
            }
            val stored = customerDao.read(customer.id)
                ?: return@put call.respond(
                    HttpStatusCode.NotFound,
                    NetworkError.CUSTOMER_NOT_FOUND.name
                )
            call.respond(HttpStatusCode.OK, stored.toDto())
        }
    }
}

/**
 * A valid token only proves who the caller is — it must also match the customer
 * id being read/written, otherwise any logged-in user could access anyone's data.
 */
private fun RoutingContext.requireOwnCustomer(customerId: String) {
    val principal = call.principal<CustomerPrincipal>()
    if (principal?.customerId != customerId) {
        throw ApiException(HttpStatusCode.Forbidden, NetworkError.FORBIDDEN)
    }
}
