package plugins.app

import extensions.plugin
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@KotlinMultiplatformConventionPlugin} invoked ***")
        with(pluginManager) {
            apply(libs.plugin("kotlinMultiplatform").pluginId)
            apply(libs.plugin("androidLibrary").pluginId)
        }
    }
}