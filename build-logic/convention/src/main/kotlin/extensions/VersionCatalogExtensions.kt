package extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugin.use.PluginDependency

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.version(name: String): VersionConstraint {
    return findVersion(name).orElseThrow {
        IllegalArgumentException("Version '$name' not found in libs.versions.toml")
    }
}

fun VersionCatalog.plugin(name: String): PluginDependency {
    return findPlugin(name).orElseThrow {
        IllegalArgumentException("Plugin '$name' not found in libs.versions.toml")
    }.get()
}

fun VersionCatalog.library(name: String): Provider<MinimalExternalModuleDependency> {
    return findLibrary(name).orElseThrow {
        IllegalArgumentException("Library '$name' not found in libs.versions.toml")
    }
}

fun VersionCatalog.bundle(name: String): Provider<ExternalModuleDependencyBundle> {
    return findBundle(name).orElseThrow {
        IllegalArgumentException("Bundle '$name' not found in libs.versions.toml")
    }
}