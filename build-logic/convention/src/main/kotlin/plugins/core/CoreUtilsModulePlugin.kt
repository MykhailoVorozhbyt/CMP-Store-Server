package plugins.core

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.kotlinMultiplatformExtension
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import utils.enums.ModuleName

class CoreUtilsModulePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@CoreUtilsModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)

        kotlinMultiplatformExtension {
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    implementation(libs.compose.ui.tooling.preview)
                }
            }
        }

        configureAndroidLibraryBase(ModuleName.CORE_UTILS.mName)
    }
}
