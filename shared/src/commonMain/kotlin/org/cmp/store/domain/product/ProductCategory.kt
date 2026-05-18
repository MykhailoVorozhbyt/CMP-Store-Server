package org.cmp.store.domain.product

import kotlinx.serialization.Serializable

@Serializable
enum class ProductCategory(
    val id: Long,
    val title: String
) {
    Protein(
        id = 1L,
        title = "Protein"
    ),
    Creatine(
        id = 2L,
        title = "Creatine"
    ),
    PreWorkout(
        id = 3L,
        title = "Pre-Workout"
    ),
    Gainers(
        id = 4L,
        title = "Gainers"
    ),
    Accessories(
        id = 5L,
        title = "Accessories"
    ),
    Unknown(0L, "")
}
