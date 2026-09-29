package configuration

import extensions.alias
import extensions.derivedNamespace
import extensions.kotlinMultiplatformExtension
import extensions.libs
import org.gradle.api.Project

fun Project.configurePureKmpLibrary() {
    pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
    configureAndroidLibraryBase(namespace = derivedNamespace, enableAndroidResources = false)
    configureIOS()
    kotlinMultiplatformExtension {
        jvm()
    }
}
