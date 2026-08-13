package plugins.core

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.module
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import utils.enums.ModuleName
import utils.enums.ModulePath

class CoreNetworkModulePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@CoreNetworkModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.serialization)

        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.CORE_NETWORK.mName)
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    module(ModulePath.SHARED)
                    module(ModulePath.CORE_DOMAIN)
                    module(ModulePath.CORE_SECURITY)
                    implementation(libs.ktor.clientCore)
                    implementation(libs.ktor.clientContentNegotiation)
                    implementation(libs.ktor.clientLogging)
                    implementation(libs.ktor.clientAuth)
                    implementation(libs.ktor.serializationKotlinxJson)
                    implementation(libs.kotlinx.serialization.json)
                }
                commonTest.dependencies {
                    // kotlin.test / coroutines.test / fakes arrive transitively via :test's api.
                    module(ModulePath.TEST)
                    implementation(libs.ktor.clientMock)
                }
                androidMain.dependencies {
                    implementation(libs.ktor.clientOkHttp)
                }
                iosMain.dependencies {
                    implementation(libs.ktor.clientDarwin)
                }
                jvmMain.dependencies {
                    implementation(libs.ktor.clientOkHttp)
                }
            }
        }
    }
}
