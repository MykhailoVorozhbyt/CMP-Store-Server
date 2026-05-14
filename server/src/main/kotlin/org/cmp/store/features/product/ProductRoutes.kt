package org.cmp.store.features.product

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.cmp.store.database.dao.ProductDao
import org.cmp.store.domain.product.ProductCategory

fun Route.productRoutes() {
    route("/product") {
        get("/discounted") {
            call.respond(ProductDao.readDiscounted())
        }
        get("/new") {
            call.respond(ProductDao.readNew(4))
        }
        get("/by-ids") {
            val ids = call.request.queryParameters["ids"]
                ?.split(",")
                ?.map(String::trim)
                ?.filter(String::isNotEmpty)
                .orEmpty()
            call.respond(ProductDao.readByIds(ids))
        }
        get("/by-category/{categoryId}") {
            val categoryId = call.parameters["categoryId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing category")
            val category = ProductCategory.entries.firstOrNull { it.id == categoryId }
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Unknown category: $categoryId")
            call.respond(ProductDao.readByCategory(category))
        }
        get("/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing product id")
            val product = ProductDao.readById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Product not found")
            call.respond(product)
        }
    }
}
