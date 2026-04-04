package org.cmp.store.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.cmp.store.domain.customer.Customer
import org.cmp.store.storage.CustomerStorage

fun Route.customerRoutes() {
    route("/customer") {
        post {
            val customer = call.receive<Customer>()
            CustomerStorage.create(customer)
            call.respond(HttpStatusCode.Created, customer)
        }
        get("/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing customer id")
            val customer = CustomerStorage.read(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Customer not found")
            call.respond(customer)
        }
        put {
            val customer = call.receive<Customer>()
            val updated = CustomerStorage.update(customer)
            if (updated) call.respond(HttpStatusCode.OK, customer)
            else call.respond(HttpStatusCode.NotFound, "Customer not found")
        }
    }
}
