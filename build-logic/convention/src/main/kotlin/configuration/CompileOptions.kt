package configuration

import extensions.appExtension
import org.gradle.api.Project
import utils.sourceCompatibilityVersion
import utils.targetCompatibilityVersion

fun Project.configureCompileOptions() = this.appExtension {
    compileOptions {
        this.sourceCompatibility = sourceCompatibilityVersion
        this.targetCompatibility = targetCompatibilityVersion
    }
}