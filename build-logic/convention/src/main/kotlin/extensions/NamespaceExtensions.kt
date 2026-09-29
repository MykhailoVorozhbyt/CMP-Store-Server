package extensions

import org.gradle.api.GradleException
import org.gradle.api.Project

private const val NAMESPACE_ROOT = "com.store"
private val VALID_PACKAGE_SEGMENT = Regex("[a-z][a-z0-9_]*")

val Project.derivedNamespace: String
    get() {
        val segments = path.split(":").filter(String::isNotBlank).map { it.replace('-', '_') }
        segments.firstOrNull { !VALID_PACKAGE_SEGMENT.matches(it) }?.let { invalid ->
            throw GradleException(
                "Module '$path' cannot derive a namespace: segment '$invalid' must be lowercase kebab-case."
            )
        }
        return (listOf(NAMESPACE_ROOT) + segments).joinToString(".")
    }
