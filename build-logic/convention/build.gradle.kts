plugins {
    `kotlin-dsl`
}

group = "com.store.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
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

        //Stores
        register("AndroidAthleticaPlus") {
            id = libs.plugins.store.android.athleticaPlus.get().pluginId
            implementationClass = "plugins.stores.AppAthleticaPlusModulePlugin"
        }
        register("AndroidNutriSport") {
            id = libs.plugins.store.android.nutriSport.get().pluginId
            implementationClass = "plugins.stores.AppNutriSportModulePlugin"
        }

        //Core
        register("CorePresentation") {
            id = libs.plugins.store.core.presentation.get().pluginId
            implementationClass = "plugins.core.CorePresentationModulePlugin"
        }
        register("CoreUtils") {
            id = libs.plugins.store.core.utils.get().pluginId
            implementationClass = "plugins.core.CoreUtilsModulePlugin"
        }
        register("CoreResources") {
            id = libs.plugins.store.core.resources.get().pluginId
            implementationClass = "plugins.core.CoreResourcesModulePlugin"
        }
        register("CoreNavigation") {
            id = libs.plugins.store.core.navigation.get().pluginId
            implementationClass = "plugins.core.CoreNavigationModulePlugin"
        }

        //Futures
        register("FeatureAuthenticationData") {
            id = libs.plugins.store.feature.authentication.data.get().pluginId
            implementationClass = "plugins.feature.FeatureAuthenticationDataModulePlugin"
        }
        register("FeatureAuthenticationDomain") {
            id = libs.plugins.store.feature.authentication.domain.get().pluginId
            implementationClass = "plugins.feature.FeatureAuthenticationDomainModulePlugin"
        }
        register("AuthenticationPresentation") {
            id = libs.plugins.store.feature.authentication.presentation.get().pluginId
            implementationClass = "plugins.feature.FeatureAuthenticationPresentationModulePlugin"
        }

        //Feature - Home
        register("FeatureHomeData") {
            id = libs.plugins.store.feature.home.data.get().pluginId
            implementationClass = "plugins.feature.FeatureHomeDataModulePlugin"
        }
        register("FeatureHomeDomain") {
            id = libs.plugins.store.feature.home.domain.get().pluginId
            implementationClass = "plugins.feature.FeatureHomeDomainModulePlugin"
        }
        register("FeatureHomePresentation") {
            id = libs.plugins.store.feature.home.presentation.get().pluginId
            implementationClass = "plugins.feature.FeatureHomePresentationModulePlugin"
        }

        //DI
        register("Di") {
            id = libs.plugins.store.di.get().pluginId
            implementationClass = "plugins.DiModulePlugin"
        }
    }
}