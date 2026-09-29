package architecture

import groovy.json.JsonSlurper
import java.io.File

private const val OWNER_PLACEHOLDER = "{owner}"
private const val EDGE_SEPARATOR = " -> "

internal data class ModuleDependency(val source: String, val target: String) {

    fun encode(): String = "$source$EDGE_SEPARATOR$target"

    companion object {
        fun decode(encoded: String): ModuleDependency {
            val (source, target) = encoded.split(EDGE_SEPARATOR)
            return ModuleDependency(source, target)
        }
    }
}

internal data class ArchitectureRule(
    val name: String,
    val source: Regex,
    val forbiddenDependency: String,
) {
    fun isViolatedBy(dependency: ModuleDependency): Boolean {
        if (!source.matches(dependency.source)) return false
        val forbidden = forbiddenDependency.replace(OWNER_PLACEHOLDER, Regex.escape(dependency.source.ownerSegment()))
        return Regex(forbidden).matches(dependency.target)
    }

    private fun String.ownerSegment(): String = split(":").filter(String::isNotBlank).getOrElse(1) { "" }
}

internal data class ArchitectureException(
    val source: String,
    val dependency: String,
    val reason: String,
) {
    fun covers(dependency: ModuleDependency): Boolean =
        source == dependency.source && this.dependency == dependency.target
}

internal data class ArchitectureViolation(val rule: String, val dependency: ModuleDependency) {
    override fun toString(): String = "${dependency.source} -> ${dependency.target}  [$rule]"
}

internal class ArchitectureRules(
    private val rules: List<ArchitectureRule>,
    private val exceptions: List<ArchitectureException>,
) {

    fun violationsIn(dependencies: List<ModuleDependency>): List<ArchitectureViolation> =
        dependencies
            .filterNot { dependency -> exceptions.any { it.covers(dependency) } }
            .flatMap { dependency ->
                rules.filter { it.isViolatedBy(dependency) }.map { ArchitectureViolation(it.name, dependency) }
            }

    fun staleExceptionsIn(dependencies: List<ModuleDependency>): List<ArchitectureException> =
        exceptions.filterNot { exception ->
            dependencies.any { dependency -> exception.covers(dependency) && rules.any { it.isViolatedBy(dependency) } }
        }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun parse(file: File): ArchitectureRules {
            val json = JsonSlurper().parse(file) as Map<String, Any?>
            val rules = (json["rules"] as List<Map<String, String>>).map {
                ArchitectureRule(
                    name = it.getValue("name"),
                    source = Regex(it.getValue("source")),
                    forbiddenDependency = it.getValue("forbiddenDependency"),
                )
            }
            val exceptions = (json["exceptions"] as List<Map<String, String>>? ?: emptyList()).map {
                ArchitectureException(
                    source = it.getValue("source"),
                    dependency = it.getValue("dependency"),
                    reason = it.getValue("reason"),
                )
            }
            return ArchitectureRules(rules, exceptions)
        }
    }
}
