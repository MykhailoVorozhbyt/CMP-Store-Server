package plugins

import architecture.ModuleDependency
import architecture.ValidateArchitectureDependenciesTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.register

private const val TASK_NAME = "validateArchitectureDependencies"
private const val RULES_PATH = "config/architecture/rules.json"
private const val REPORT_PATH = "reports/architecture/violations.txt"

class ArchitectureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        println("*** ${this@ArchitectureConventionPlugin} invoked ***")
        if (this != rootProject) {
            throw GradleException("store.architecture must be applied to the root project, not '$path'.")
        }
        tasks.register<ValidateArchitectureDependenciesTask>(TASK_NAME) {
            group = "verification"
            description = "Checks module dependencies against $RULES_PATH."
            rulesFile.set(layout.projectDirectory.file(RULES_PATH))
            reportFile.set(layout.buildDirectory.file(REPORT_PATH))
            moduleDependencies.set(provider { collectModuleDependencies().map(ModuleDependency::encode) })
        }
    }

    private fun Project.collectModuleDependencies(): List<ModuleDependency> {
        val unconfigured = subprojects.filterNot { it.state.executed }.map { it.path }
        if (unconfigured.isNotEmpty()) {
            throw GradleException(
                "Configure-on-demand skipped $unconfigured. Run the task by name, not by path: ./gradlew $TASK_NAME"
            )
        }
        return subprojects
            .flatMap { module ->
                module.configurations
                    .filter { it.isProductionDependencyBucket() }
                    .flatMap { configuration -> configuration.dependencies.withType(ProjectDependency::class.java) }
                    .map { ModuleDependency(source = module.path, target = it.path) }
            }
            .filterNot { it.source == it.target }
            .distinct()
            .sortedBy { it.encode() }
    }

    private fun Configuration.isProductionDependencyBucket(): Boolean {
        val isDeclarationBucket = name == "implementation" || name == "api" ||
            name.endsWith("Implementation") || name.endsWith("Api")
        return isDeclarationBucket && !name.contains("test", ignoreCase = true)
    }
}
