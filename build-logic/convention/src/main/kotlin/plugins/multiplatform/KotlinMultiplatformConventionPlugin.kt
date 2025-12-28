package plugins.multiplatform

import extensions.applyPlugins
import extensions.libs
import extensions.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.enums.PluginName

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        applyPlugins {
            listOf(
                libs.plugin(PluginName.KOTLIN_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.KOTLIN_MULTIPLATFORM_LIBRARY.pName).pluginId,
            )
        }
    }
}