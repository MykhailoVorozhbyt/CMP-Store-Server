package plugins.core

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.composeExtension
import extensions.kotlinMultiplatformExtension
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.compose.resources.ResourcesExtension
import utils.enums.ModuleName

class CoreResourcesModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@CoreResourcesModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.composeMultiplatform)
        pluginManager.alias(libs.plugins.composeCompiler)

        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.CORE_RESOURCES.mName)
            configureIOS()
            jvm()

            sourceSets {
                androidMain.dependencies {
                    implementation(libs.androidx.activity.compose)
                }
                commonMain.dependencies {
                    implementation(libs.compose.runtime)
                    implementation(libs.compose.foundation)
                    implementation(libs.compose.material3)
                    implementation(libs.compose.components.resources)
                }
            }
        }
        composeExtension {
            extensions.configure<ResourcesExtension> {
                publicResClass = true
                packageOfResClass = "com.store.core.resources"
                generateResClass = always
            }
        }
    }
}
