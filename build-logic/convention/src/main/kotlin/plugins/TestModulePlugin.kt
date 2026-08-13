package plugins

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.kotlinMultiplatformExtension
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import utils.enums.ModuleName
import utils.enums.ModulePath

class TestModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@TestModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)

        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.TEST.mName)
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    api(project(ModulePath.CORE_PRESENTATION.path))
                    api(project(ModulePath.CORE_NETWORK.path))
                    api(project(ModulePath.CORE_DOMAIN.path))
                    api(project(ModulePath.CORE_SECURITY.path))
                    api(project(ModulePath.FEATURE_AUTHENTICATION_DOMAIN.path))
                    api(project(ModulePath.SHARED.path))
                    api(libs.kotlin.test)
                    api(libs.kotlinx.coroutines.test)
                    api(libs.turbine)
                    api(libs.koin.test)
                    api(libs.compose.ui.test)
                }
                jvmMain.dependencies {
                    api(libs.kotlin.testJunit)
                }
                androidMain.dependencies {
                    api(libs.kotlin.testJunit)
                }
            }

        }
    }
}