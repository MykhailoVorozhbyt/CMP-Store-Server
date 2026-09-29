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
        register("AppShared") {
            id = libs.plugins.store.app.shared.get().pluginId
            implementationClass = "plugins.app.AppSharedModulePlugin"
        }
        register("Test") {
            id = libs.plugins.store.test.get().pluginId
            implementationClass = "plugins.TestModulePlugin"
        }
        register("Server") {
            id = libs.plugins.store.server.get().pluginId
            implementationClass = "plugins.ServerModulePlugin"
        }

        //App - store KMP library
        register("AppAthleticaPlusLibrary") {
            id = libs.plugins.store.app.athleticaPlus.library.get().pluginId
            implementationClass = "plugins.app.AthleticaPlusModulePlugin"
        }
        register("AppNutriSportLibrary") {
            id = libs.plugins.store.app.nutriSport.library.get().pluginId
            implementationClass = "plugins.app.NutriSportModulePlugin"
        }
        //App - store Android app
        register("AppAthleticaPlusAndroidApp") {
            id = libs.plugins.store.app.athleticaPlus.androidApp.get().pluginId
            implementationClass = "plugins.app.AthleticaPlusAndroidAppPlugin"
        }
        register("AppNutriSportAndroidApp") {
            id = libs.plugins.store.app.nutriSport.androidApp.get().pluginId
            implementationClass = "plugins.app.NutriSportAndroidAppPlugin"
        }
        //App - store desktop app
        register("AppAthleticaPlusDesktopApp") {
            id = libs.plugins.store.app.athleticaPlus.desktopApp.get().pluginId
            implementationClass = "plugins.app.AthleticaPlusDesktopAppPlugin"
        }
        register("AppNutriSportDesktopApp") {
            id = libs.plugins.store.app.nutriSport.desktopApp.get().pluginId
            implementationClass = "plugins.app.NutriSportDesktopAppPlugin"
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

        //Component layers
        register("ComponentModel") {
            id = libs.plugins.store.component.model.get().pluginId
            implementationClass = "plugins.component.ComponentModelPlugin"
        }
        register("ComponentDomainApi") {
            id = libs.plugins.store.component.domainApi.get().pluginId
            implementationClass = "plugins.component.ComponentDomainApiPlugin"
        }
        register("ComponentUseCase") {
            id = libs.plugins.store.component.usecase.get().pluginId
            implementationClass = "plugins.component.ComponentUseCasePlugin"
        }
        register("ComponentData") {
            id = libs.plugins.store.component.data.get().pluginId
            implementationClass = "plugins.component.ComponentDataPlugin"
        }

        //Feature - presentation
        register("FeaturePresentation") {
            id = libs.plugins.store.feature.presentation.get().pluginId
            implementationClass = "plugins.feature.FeaturePresentationPlugin"
        }

        //Capabilities
        register("FeatureUiTest") {
            id = libs.plugins.store.feature.uiTest.get().pluginId
            implementationClass = "plugins.capability.FeatureUiTestCapabilityPlugin"
        }
        register("FirebaseAuth") {
            id = libs.plugins.store.firebase.auth.get().pluginId
            implementationClass = "plugins.capability.FirebaseAuthCapabilityPlugin"
        }
        register("KmpAuthGoogle") {
            id = libs.plugins.store.kmpauth.google.get().pluginId
            implementationClass = "plugins.capability.KmpAuthGoogleCapabilityPlugin"
        }

        //Architecture
        register("Architecture") {
            id = libs.plugins.store.architecture.get().pluginId
            implementationClass = "plugins.ArchitectureConventionPlugin"
        }

        //DI
        register("Di") {
            id = libs.plugins.store.di.get().pluginId
            implementationClass = "plugins.DiModulePlugin"
        }
    }
}
