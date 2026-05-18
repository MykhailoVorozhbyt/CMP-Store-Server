package com.store.core.presentation.utils.product

import com.store.core.resources.Res
import com.store.core.resources.Resources
import com.store.core.resources.ic_measurement_gallon
import com.store.core.resources.ic_measurement_gram
import com.store.core.resources.ic_measurement_kilogram
import com.store.core.resources.ic_measurement_liter
import com.store.core.resources.ic_measurement_milliliter
import com.store.core.resources.ic_measurement_portion
import com.store.core.resources.measurement_gallons
import com.store.core.resources.measurement_grams
import com.store.core.resources.measurement_kilograms
import com.store.core.resources.measurement_liters
import com.store.core.resources.measurement_milliliters
import com.store.core.resources.measurement_portions
import com.store.core.resources.measurement_single_item
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class Measurement(
    val id: Long,
    val textResource: StringResource,
    val iconResource: DrawableResource
) {
    KILOGRAM(1, Res.string.measurement_kilograms, Res.drawable.ic_measurement_kilogram),
    LITER(2, Res.string.measurement_liters, Res.drawable.ic_measurement_liter),
    MILLILITER(3, Res.string.measurement_milliliters, Res.drawable.ic_measurement_milliliter),
    GRAM(4, Res.string.measurement_grams, Res.drawable.ic_measurement_gram),
    GALLON(5, Res.string.measurement_gallons, Res.drawable.ic_measurement_gallon),
    PORTION(6, Res.string.measurement_portions, Res.drawable.ic_measurement_portion),
    SINGLE_ITEM(7, Res.string.measurement_single_item, Res.drawable.ic_measurement_portion);

    companion object {
//        fun getMeasurementsChipIndexByTextId(id: Int) =
//            MeasurementsList.firstOrNull { it.textId == id } ?: KILOGRAM

//        fun getMeasurementById(id: Long) = MeasurementsList.firstOrNull { it.id == id } ?: KILOGRAM
    }
}

val MeasurementsList = listOf(
    Measurement.KILOGRAM,
    Measurement.LITER,
    Measurement.MILLILITER,
    Measurement.GRAM,
    Measurement.GALLON,
    Measurement.PORTION,
    Measurement.SINGLE_ITEM
)