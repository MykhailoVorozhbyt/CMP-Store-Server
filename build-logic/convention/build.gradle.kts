plugins {
    `kotlin-dsl`
}

group = "com.store.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    //TODO: for the future
    //lintChecks(libs.androidx.lint.gradle)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("KotlinMultiplatform") {
            id = libs.plugins.store.kotlinMultiplatform.get().pluginId
            implementationClass = "plugins.multiplatform.KotlinMultiplatformConventionPlugin"
        }
        register("ComposeMultiplatform") {
            id = libs.plugins.store.composeMultiplatform.get().pluginId
            implementationClass = "plugins.multiplatform.ComposeMultiplatformConventionPlugin"
        }
        register("Shared") {
            id = libs.plugins.store.shared.get().pluginId
            implementationClass = "plugins.SharedModulePlugin"
        }
        register("ComposeApp") {
            id = libs.plugins.store.composeApp.get().pluginId
            implementationClass = "plugins.ComposeAppModulePlugin"
        }

        //Android stores
        register("AndroidAthleticaPlus") {
            id = libs.plugins.store.android.athleticaPlus.get().pluginId
            implementationClass = "plugins.stores.AndroidAthleticaPlusModulePlugin"
        }
        register("AndroidNutriSport") {
            id = libs.plugins.store.android.nutriSport.get().pluginId
            implementationClass = "plugins.stores.AndroidNutriSportModulePlugin"
        }

        //Desktop stores
        register("DesktopAthleticaPlus") {
            id = libs.plugins.store.desktop.athleticaPlus.get().pluginId
            implementationClass = "plugins.stores.DesktopAthleticaPlusModulePlugin"
        }
        register("DesktopNutriSport") {
            id = libs.plugins.store.desktop.nutriSport.get().pluginId
            implementationClass = "plugins.stores.DesktopNutriSportModulePlugin"
        }

        //Core
        register("CorePresentation") {
            id = libs.plugins.store.core.presentation.get().pluginId
            implementationClass = "plugins.core.CorePresentationModulePlugin"
        }
    }
}