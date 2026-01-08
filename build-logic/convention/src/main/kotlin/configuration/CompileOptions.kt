package configuration

import extensions.androidExtension
import org.gradle.api.Project
import utils.sourceCompatibilityVersion
import utils.targetCompatibilityVersion

fun Project.configureCompileOptions(isCoreLibraryDesugaringEnabled: Boolean = false) = this.androidExtension {
    compileOptions {
        this.sourceCompatibility = sourceCompatibilityVersion
        this.targetCompatibility = targetCompatibilityVersion
        this.isCoreLibraryDesugaringEnabled = isCoreLibraryDesugaringEnabled
    }
}