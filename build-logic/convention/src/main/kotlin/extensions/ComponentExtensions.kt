package extensions

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import utils.enums.ComponentLayer
import utils.enums.ComponentName

internal const val COMPONENT_ROOT = "component"

@JvmInline
value class ComponentModule(val path: String)

fun component(name: ComponentName, layer: ComponentLayer): ComponentModule =
    ComponentModule(":$COMPONENT_ROOT:${name.dirName}:${layer.dirName}")

fun KotlinDependencyHandler.module(component: ComponentModule) =
    implementation(project(component.path))

fun KotlinDependencyHandler.apiModule(component: ComponentModule) =
    api(project(component.path))

internal fun Project.owningComponent(layer: ComponentLayer): ComponentName {
    val segments = path.split(":").filter(String::isNotBlank)
    if (segments.size != 3 || segments[0] != COMPONENT_ROOT || segments[2] != layer.dirName) {
        throw GradleException(
            "The ${layer.name} component plugin expects a module at ':$COMPONENT_ROOT:<name>:${layer.dirName}', " +
                "but was applied to '$path'."
        )
    }
    return ComponentName.fromDirName(segments[1]) ?: throw GradleException(
        "Component '${segments[1]}' ($path) is not listed in ComponentName — add it there first."
    )
}
