package plugins.component

import configuration.configurePureKmpLibrary
import extensions.kotlinMultiplatformExtension
import extensions.owningComponent
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.enums.ComponentLayer
import utils.enums.ComponentName

abstract class ComponentLayerPlugin(private val layer: ComponentLayer) : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        println("*** ${this@ComponentLayerPlugin} invoked ***")
        val owner = owningComponent(layer)
        configurePureKmpLibrary()
        kotlinMultiplatformExtension {
            configureLayer(target, owner)
        }
    }

    protected abstract fun KotlinMultiplatformExtension.configureLayer(target: Project, owner: ComponentName)
}
