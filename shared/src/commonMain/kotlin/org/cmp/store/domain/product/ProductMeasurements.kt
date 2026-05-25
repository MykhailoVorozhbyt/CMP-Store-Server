package org.cmp.store.domain.product

import kotlinx.serialization.Serializable

@Serializable
data class ProductMeasurements(
    val id: Long,
    val name: String,
    val iconId: Int
)
