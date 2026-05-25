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

class SharedModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@SharedModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.composeMultiplatform)
        pluginManager.alias(libs.plugins.composeCompiler)
        pluginManager.alias(libs.plugins.serialization)

        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.SHARED.mName)
            configureIOS()
            jvm()

            sourceSets {
                androidMain.dependencies {
                    implementation(libs.androidx.activity.compose)
                    implementation(libs.ktor.clientOkHttp)
                }
                commonMain.dependencies {
                    implementation(libs.compose.runtime)
                    implementation(libs.compose.foundation)
                    implementation(libs.compose.material3)
                    implementation(libs.koin.core)
                    implementation(libs.koin.compose)
                    implementation(libs.jetbrains.navigation3.ui)
                    implementation(libs.kotlinx.serialization.json)
                    implementation(libs.ktor.clientCore)
                    implementation(libs.ktor.clientContentNegotiation)
                    implementation(libs.ktor.clientLogging)
                    implementation(libs.ktor.serializationKotlinxJson)
                }
                iosMain.dependencies {
                    implementation(libs.ktor.clientDarwin)
                }
                jvmMain.dependencies {
                    implementation(libs.ktor.clientOkHttp)
                }
                commonTest.dependencies {
                    implementation(libs.kotlin.test)
                    implementation(libs.koin.test)
                }
            }
        }
    }
}
