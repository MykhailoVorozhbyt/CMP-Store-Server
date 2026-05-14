package extensions

import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.androidRuntimeClasspath(dep: Any) {
    add("androidRuntimeClasspath", dep)
}