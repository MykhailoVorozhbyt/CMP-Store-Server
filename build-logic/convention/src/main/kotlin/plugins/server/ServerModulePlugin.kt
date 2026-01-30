/*
package plugins.server

import extensions.applyPlugins
import extensions.implementation
import extensions.libs
import extensions.plugin
import extensions.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.plugins.JavaApplication
import org.gradle.internal.declarativedsl.intrinsics.listOf
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import utils.enums.ModuleName
import utils.enums.PluginName


class ServerModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        applyPlugins {
            listOf(
                libs.plugin(PluginName.KOTLIN_JVM.pName).pluginId,
                libs.plugin(PluginName.KTOR.pName).pluginId,
            )
        }

        group = ModuleName.APP.mName
        version = "1.0.0"
        extensions.configure<JavaApplication> {
            mainClass.set("org.cmp.store.ApplicationKt")
            extensions.configure<ExtraPropertiesExtension> {
                val isDevelopment: Boolean = has("development")
                applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
            }
        }

        dependencies {
            implementation(project)

            implementation(libs.logback)
            implementation(libs.ktor.serverCore)
            implementation(libs.ktor.serverNetty)
            testImplementation(libs.ktor.serverTestHost)
            testImplementation(libs.kotlin.testJunit)
        }
    }
}*/
