package org.cmp.store.features.customer

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.cmp.store.database.dao.CustomerDao
import org.cmp.store.domain.customer.Customer
import org.cmp.store.network.NetworkError

fun Route.customerRoutes() {
    route("/customer") {
        post {
            val customer = call.receive<Customer>()
            val userExists = CustomerDao.exists(customer.id)
            if (userExists) {
                call.respond(HttpStatusCode.Conflict, NetworkError. USER_ALREADY_EXISTS.message)
            } else {
                CustomerDao.create(customer)
                call.respond(HttpStatusCode.Created, customer)
            }
        }
        get("/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    NetworkError.MISSING_CUSTOMER_ID
                )
            val customer = CustomerDao.read(id)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    NetworkError.CUSTOMER_NOT_FOUND.message
                )
            call.respond(customer)
        }
        put {
            val customer = call.receive<Customer>()
            val updated = CustomerDao.update(customer)
            if (updated) call.respond(HttpStatusCode.OK, customer)
            else call.respond(HttpStatusCode.NotFound, NetworkError.CUSTOMER_NOT_FOUND.message)
        }
    }
}
