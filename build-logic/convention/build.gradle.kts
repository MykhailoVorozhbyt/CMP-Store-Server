plugins {
    `kotlin-dsl`
}

group = "com.store.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.multiplatform.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)

    // Workaround for version catalog working inside precompiled scripts https://github.com/gradle/gradle/issues/15383
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
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
        register("Test") {
            id = libs.plugins.store.test.get().pluginId
            implementationClass = "plugins.TestModulePlugin"
        }
        register("Server") {
            id = libs.plugins.store.server.get().pluginId
            implementationClass = "plugins.ServerModulePlugin"
        }

        //Stores - KMP library
        register("AndroidAthleticaPlus") {
            id = libs.plugins.store.android.athleticaPlus.kmp.get().pluginId
            implementationClass = "plugins.stores.AppAthleticaPlusModulePlugin"
        }
        register("AndroidNutriSport") {
            id = libs.plugins.store.android.nutriSport.kmp.get().pluginId
            implementationClass = "plugins.stores.AppNutriSportModulePlugin"
        }
        //Stores - pure Android app
        register("AndroidAthleticaPlusApp") {
            id = libs.plugins.store.android.athleticaPlus.androidApp.get().pluginId
            implementationClass = "plugins.stores.AthleticaPlusAndroidAppPlugin"
        }
        register("AndroidNutriSportApp") {
            id = libs.plugins.store.android.nutriSport.androidApp.get().pluginId
            implementationClass = "plugins.stores.NutriSportAndroidAppPlugin"
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
        register("CoreData") {
            id = libs.plugins.store.core.data.get().pluginId
            implementationClass = "plugins.core.CoreDataModulePlugin"
        }
        register("CoreDomain") {
            id = libs.plugins.store.core.domain.get().pluginId
            implementationClass = "plugins.core.CoreDomainModulePlugin"
        }
        register("CoreSecurity") {
            id = libs.plugins.store.core.security.get().pluginId
            implementationClass = "plugins.core.CoreSecurityModulePlugin"
        }
        register("CoreNetwork") {
            id = libs.plugins.store.core.network.get().pluginId
            implementationClass = "plugins.core.CoreNetworkModulePlugin"
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
