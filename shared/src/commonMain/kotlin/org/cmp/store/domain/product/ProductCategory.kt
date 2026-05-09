package org.cmp.store.domain.product

import kotlinx.serialization.Serializable

@Serializable
enum class ProductCategory(
    val title: String,
//    val color: Color
) {
    Protein(
        title = "Protein",
//        color = CategoryYellow
    ),
    Creatine(
        title = "Creatine",
//        color = CategoryBlue
    ),
    PreWorkout(
        title = "Pre-Workout",
//        color = CategoryGreen
    ),
    Gainers(
        title = "Gainers",
//        color = CategoryPurple
    ),
    Accessories(
        title = "Accessories",
//        color = CategoryRed
    ),
    Unknown("")
}
