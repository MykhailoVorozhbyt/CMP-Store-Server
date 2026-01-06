package extensions

import org.gradle.api.Project
import org.jetbrains.compose.ComposePlugin

val Project.composeDep: ComposePlugin.Dependencies
    get() = ComposePlugin.Dependencies(this)