package plugins.stores

import configuration.configureAndroidLibraryBase
import configuration.configureDesktopApplication
import configuration.configureIOS
import extensions.alias
import extensions.composeDep
import extensions.kotlinMultiplatformExtension
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.gradle.kotlin.dsl.invoke
import utils.enums.ModuleName
import utils.enums.ModulePath

class AppAthleticaPlusModulePlugin : StoreModulePlugin() {
    override val moduleName: ModuleName = ModuleName.ATHLETICA_PLUS_KMP
    override val mainClass = "com.store.athletica_plus.MainKt"
    override val packageName = "com.store.athletica_plus"
    override val appVersion = "1.0.0"
}

class AppNutriSportModulePlugin : StoreModulePlugin() {
    override val moduleName: ModuleName = ModuleName.NUTRI_SPORT_KMP
    override val mainClass = "com.store.nutri_sport.MainKt"
    override val packageName = "com.store.nutri_sport"
    override val appVersion = "1.0.0"
}

abstract class StoreModulePlugin : Plugin<Project> {
    abstract val moduleName: ModuleName
    abstract val mainClass: String
    abstract val packageName: String
    abstract val appVersion: String

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun apply(target: Project) = with(target) {
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.store.composeMultiplatform)

        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(moduleName.mName)
            configureIOS()
            jvm {
                mainRun {
                    mainClass.set(this@StoreModulePlugin.mainClass)
                }
            }

            sourceSets {
                commonMain.dependencies {
                    implementation(project(ModulePath.COMPOSE_APP.path))
                    implementation(project(ModulePath.CORE_PRESENTATION.path))
                    implementation(libs.compose.components.resources)
                    implementation(libs.compose.ui.tooling.preview)
                    implementation(libs.koin.core)
                    implementation(libs.koin.compose)
                    implementation(libs.firebase.app)
                    implementation(libs.kmpauth.google)
                }
                androidMain.dependencies {
                    implementation(project.dependencies.platform(libs.firebase.bom))
                }
                jvmMain.dependencies {
                    implementation(composeDep.desktop.currentOs)
                }
            }
        }

        configureDesktopApplication(
            mainClass = mainClass,
            packageName = packageName,
            version = appVersion,
        )
    }
}
