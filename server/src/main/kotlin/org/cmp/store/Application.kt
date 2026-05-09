package org.cmp.store

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import org.cmp.store.database.dao.ProductDao
import org.cmp.store.database.DatabaseFactory
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.plugins.configureRouting
import org.cmp.store.plugins.configureSerialization
import org.cmp.store.features.customer.customerRoutes
import org.cmp.store.features.product.productRoutes

fun main() {
    DatabaseFactory.init()
    seedProducts()
    embeddedServer(
        factory = Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    routing {
        configureRouting()
        customerRoutes()
        productRoutes()
    }
}

private fun seedProducts() {
    kotlinx.coroutines.runBlocking {
        ProductDao.seed(
            listOf(
                Product(
                    id = "protein-whey-1",
                    title = "Whey Protein Gold",
                    description = "Fast-absorbing whey protein for post-workout recovery.",
                    thumbnail = "https://example.com/images/protein-whey-1.png",
                    category = ProductCategory.Protein.title,
                    flavors = listOf("Chocolate", "Vanilla"),
                    weight = 900,
                    price = 54.99,
                    isPopular = true,
                    isDiscounted = true,
                ),
                Product(
                    id = "protein-isolate-1",
                    title = "Isolate Pro",
                    description = "High-purity isolate with minimal carbs and fats.",
                    thumbnail = "https://example.com/images/protein-isolate-1.png",
                    category = ProductCategory.Protein.title,
                    flavors = listOf("Strawberry"),
                    weight = 750,
                    price = 69.99,
                    isNew = true,
                ),
                Product(
                    id = "creatine-1",
                    title = "Creatine Monohydrate",
                    description = "Classic micronized creatine for strength and power.",
                    thumbnail = "https://example.com/images/creatine-1.png",
                    category = ProductCategory.Creatine.title,
                    weight = 300,
                    price = 24.99,
                    isDiscounted = true,
                ),
                Product(
                    id = "creatine-2",
                    title = "Creatine Creapure",
                    description = "Premium-grade creatine for daily performance support.",
                    thumbnail = "https://example.com/images/creatine-2.png",
                    category = ProductCategory.Creatine.title,
                    weight = 500,
                    price = 29.99,
                    isPopular = true,
                ),
                Product(
                    id = "preworkout-1",
                    title = "Pre-Workout Ignite",
                    description = "Energy and focus blend before training.",
                    thumbnail = "https://example.com/images/preworkout-1.png",
                    category = ProductCategory.PreWorkout.title,
                    flavors = listOf("Berry Blast"),
                    weight = 420,
                    price = 34.99,
                ),
                Product(
                    id = "preworkout-2",
                    title = "Pump Matrix",
                    description = "Pre-workout formula focused on pump and endurance.",
                    thumbnail = "https://example.com/images/preworkout-2.png",
                    category = ProductCategory.PreWorkout.title,
                    flavors = listOf("Mango", "Cola"),
                    weight = 390,
                    price = 39.99,
                    isDiscounted = true,
                    isNew = true,
                ),
                Product(
                    id = "gainer-1",
                    title = "Mass Gainer X",
                    description = "High-calorie gainer for muscle mass support.",
                    thumbnail = "https://example.com/images/gainer-1.png",
                    category = ProductCategory.Gainers.title,
                    flavors = listOf("Cookies"),
                    weight = 3000,
                    price = 44.99,
                ),
                Product(
                    id = "shaker-1",
                    title = "Metal Shaker",
                    description = "Durable accessory for protein and pre-workout mixes.",
                    thumbnail = "https://example.com/images/shaker-1.png",
                    category = ProductCategory.Accessories.title,
                    price = 9.99,
                    isPopular = true,
                ),
            ),
        )
    }
}
