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

class CoreSecurityModulePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@CoreSecurityModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)

        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.CORE_SECURITY.mName)
            configureIOS()
            jvm()

            sourceSets {
                androidMain.dependencies {
                    implementation(libs.kotlinx.coroutines.core)
                }
                jvmMain.dependencies {
                    implementation(libs.kotlinx.coroutines.core)
                }
            }
        }
    }
}
