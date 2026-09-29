package org.cmp.store.features.product.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.cmp.store.database.dao.ProductDao
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.network.NetworkError
import org.cmp.store.utils.ApiException
import org.cmp.store.utils.Products
import org.koin.ktor.ext.inject

fun Route.productRoutes() {
    val productDao by inject<ProductDao>()
    get<Products.Discounted> {
        call.respond(productDao.readDiscounted())
    }
    get<Products.New> {
        call.respond(productDao.readNew(4))
    }
    get<Products.ByIds> { resource ->
        val ids = resource.ids
            .map(String::trim)
            .filter(String::isNotEmpty)
        call.respond(productDao.readByIds(ids))
    }
    get<Products.ByCategory> { resource ->
        val category = ProductCategory.entries.firstOrNull { it.id == resource.categoryId }
            ?: throw ApiException(HttpStatusCode.BadRequest, NetworkError.INVALID_PRODUCT_CATEGORY)
        call.respond(productDao.readByCategory(category))
    }
    get<Products.Id> { resource ->
        val product = productDao.readById(resource.id)
            ?: throw ApiException(HttpStatusCode.NotFound, NetworkError.PRODUCT_NOT_FOUND)
        call.respond(product)
    }
}
