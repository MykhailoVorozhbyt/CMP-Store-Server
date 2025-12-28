plugins {
    `kotlin-dsl`
}

group = "com.store.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = libs.plugins.store.kotlinMultiplatform.get().pluginId
            implementationClass = "plugins.multiplatform.KotlinMultiplatformConventionPlugin"
        }
        register("composeMultiplatform") {
            id = libs.plugins.store.composeMultiplatform.get().pluginId
            implementationClass = "plugins.multiplatform.ComposeMultiplatformConventionPlugin"
        }
        register("shared") {
            id = libs.plugins.store.shared.get().pluginId
            implementationClass = "plugins.SharedModulePlugin"
        }
    }
}