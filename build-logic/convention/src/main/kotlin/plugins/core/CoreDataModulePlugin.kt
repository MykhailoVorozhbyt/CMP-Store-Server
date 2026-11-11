package plugins.core

import androidx.room.gradle.RoomExtension
import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.module
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import utils.enums.ModuleName
import utils.enums.ModulePath

private const val SCHEMA_DIR = "schemas"
private val roomKspConfigurations = listOf("kspAndroid", "kspIosArm64", "kspIosSimulatorArm64", "kspJvm")

class CoreDataModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@CoreDataModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.ksp)
        pluginManager.alias(libs.plugins.room)

        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.CORE_DATA.mName, enableAndroidResources = false)
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    module(ModulePath.SHARED)
                    module(ModulePath.CORE_SECURITY)
                    implementation(libs.room.runtime)
                    implementation(libs.sqlite.bundled)
                    implementation(libs.kotlinx.coroutines.core)
                    implementation(libs.koin.core)
                }
                androidMain.dependencies {
                    implementation(libs.koin.android)
                }
                jvmTest.dependencies {
                    implementation(libs.kotlin.test)
                    implementation(libs.kotlinx.coroutines.test)
                }
            }
        }
        extensions.configure<RoomExtension> {
            schemaDirectory("$projectDir/$SCHEMA_DIR")
        }
        dependencies {
            roomKspConfigurations.forEach { configuration -> add(configuration, libs.room.compiler) }
        }
    }
}
