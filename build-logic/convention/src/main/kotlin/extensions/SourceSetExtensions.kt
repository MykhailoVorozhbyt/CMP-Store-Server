package extensions

import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

/**
 * Configures an existing Kotlin source set by [name]: optionally attaches a shared source directory
 * ([srcDir]) and declares its [dependencies].
 *
 * Handy when two source sets from different trees (e.g. `jvmTest` and `androidDeviceTest`) need to
 * compile the same files without a cross-tree `dependsOn` edge — point both at the same [srcDir].
 */
fun NamedDomainObjectContainer<KotlinSourceSet>.sourceSet(
    name: String,
    srcDir: String? = null,
    dependencies: KotlinDependencyHandler.() -> Unit = {}
) = getByName(name).apply {
    srcDir?.let { kotlin.srcDir(it) }
    dependencies(dependencies)
}
