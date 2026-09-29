package plugins.app

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.composeExtension
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.module
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.compose.resources.ResourcesExtension
import utils.enums.ModuleName
import utils.enums.ModulePath

class AthleticaPlusModulePlugin : StoreModulePlugin() {
    override val moduleName: ModuleName = ModuleName.ATHLETICA_PLUS_KMP
    override val iosFrameworkName = "StoresAthletica-plus"
    override val resourcesPackage = "com.store.athletica_plus.resources"
}

class NutriSportModulePlugin : StoreModulePlugin() {
    override val moduleName: ModuleName = ModuleName.NUTRI_SPORT_KMP
    override val iosFrameworkName = "StoresNutri-sport"
    override val resourcesPackage = "com.store.nutri_sport.resources"
}

abstract class StoreModulePlugin : Plugin<Project> {
    abstract val moduleName: ModuleName
    abstract val iosFrameworkName: String
    abstract val resourcesPackage: String

    override fun apply(target: Project) = with(target) {
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.store.composeMultiplatform)

        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(moduleName.mName)
            configureIOS(frameworkName = iosFrameworkName)
            jvm()

            sourceSets {
                commonMain.dependencies {
                    module(ModulePath.APP_SHARED)
                    module(ModulePath.CORE_PRESENTATION)
                    implementation(libs.compose.components.resources)
                    implementation(libs.compose.ui.tooling.preview)
                    implementation(libs.koin.core)
                    implementation(libs.koin.compose)
                    implementation(libs.firebase.app)
                }
                androidMain.dependencies {
                    implementation(project.dependencies.platform(libs.firebase.bom))
                }
            }
        }
        composeExtension {
            extensions.configure<ResourcesExtension> {
                packageOfResClass = resourcesPackage
            }
        }
    }
}
