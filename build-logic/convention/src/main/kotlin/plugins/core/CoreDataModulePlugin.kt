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

class CoreDataModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.CORE_DATA.mName)
            configureIOS()
            jvm()
            sourceSets {
                commonMain.dependencies {
                    module(ModulePath.SHARED)
                }
            }
        }
    }
}
