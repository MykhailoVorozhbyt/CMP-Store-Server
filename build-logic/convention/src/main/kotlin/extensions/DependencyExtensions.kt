package extensions

import org.gradle.api.InvalidUserDataException
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import utils.enums.ModulePath

fun DependencyHandler.androidRuntimeClasspath(dep: Any) {
    add("androidRuntimeClasspath", dep)
}

fun KotlinDependencyHandler.module(path: ModulePath) =
    implementation(project(path.path))

fun DependencyHandlerScope.implementation(dep: Any) {
    safeAdd("implementation", dep)
}

fun DependencyHandlerScope.testImplementation(dep: Any) {
    safeAdd("testImplementation", dep)
}

fun DependencyHandlerScope.module(path: ModulePath) =
    implementation(project(path.path))

private fun DependencyHandlerScope.safeAdd(
    configuration: String,
    dependency: Any,
) {
    when (dependency) {
        is Provider<*> -> {
            val value = dependency.orNull
            if (value != null) {
                safeAdd(configuration, value)
            }
        }

        is Collection<*> -> {
            dependency.forEach {
                if (it != null) {
                    safeAdd(configuration, it)
                }
            }
        }

        else -> {
            try {
                add(configuration, dependency)
            } catch (e: UnknownConfigurationException) {
                throw IllegalArgumentException(
                    "Unknown configuration '$configuration' for dependency: ${dependency.javaClass.name}",
                    e
                )
            } catch (e: InvalidUserDataException) {
                throw IllegalArgumentException(
                    "Invalid dependency type: ${dependency.javaClass.name}",
                    e
                )
            } catch (e: Exception) {
                throw IllegalArgumentException(
                    "Unsupported dependency type: ${dependency.javaClass.name}",
                    e
                )
            }
        }
    }
}
