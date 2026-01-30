package configuration

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

@Suppress("EnumEntryName")
enum class FlavorDimension {
    store
}

@Suppress("EnumEntryName")
enum class StoreFlavor(
    val dimension: FlavorDimension,
    val applicationIdSuffix: String
) {
    ua(FlavorDimension.store, ".ua"),
    com(FlavorDimension.store, ".com"),
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: StoreFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            StoreFlavor.values().forEach { niaFlavor ->
                register(niaFlavor.name) {
                    dimension = niaFlavor.dimension.name
                    flavorConfigurationBlock(this, niaFlavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        applicationIdSuffix = niaFlavor.applicationIdSuffix
                        versionNameSuffix = niaFlavor.name
                    }
                }
            }
        }
    }
}