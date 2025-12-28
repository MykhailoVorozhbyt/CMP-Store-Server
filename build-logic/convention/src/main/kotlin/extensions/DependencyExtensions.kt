package extensions

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import utils.enums.LibraryName
import kotlin.collections.forEach

/**
 * These functions will be used in the future
 * */

fun DependencyHandlerScope.api(dep: Any) {
    safeAdd("api", dep)
}

fun DependencyHandlerScope.implementation(dep: Any) {
    safeAdd("implementation", dep)
}

fun DependencyHandlerScope.testImplementation(dep: Any) {
    safeAdd("testImplementation", dep)
}

fun DependencyHandlerScope.androidTestImplementation(dep: Any) {
    safeAdd("androidTestImplementation", dep)
}

fun DependencyHandlerScope.debugImplementation(dep: Any) {
    safeAdd("debugImplementation", dep)
}

fun DependencyHandlerScope.custom(configuration: String, dep: Any) {
    safeAdd(configuration, dep)
}

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
                throw IllegalArgumentException("Unsupported dependency type: ${dependency.javaClass.name}", e)
            }
        }
    }
}

fun Project.applyDependencies(list: List<LibraryName>) {
    dependencies {
        list.forEach { library ->
            implementation(this@applyDependencies.libs.findLibrary(library.lName).get())
        }
    }
}