package architecture

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class ValidateArchitectureDependenciesTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val rulesFile: RegularFileProperty

    @get:Input
    abstract val moduleDependencies: ListProperty<String>

    @get:OutputFile
    abstract val reportFile: RegularFileProperty

    @TaskAction
    fun validate() {
        val rules = ArchitectureRules.parse(rulesFile.get().asFile)
        val dependencies = moduleDependencies.get().map(ModuleDependency::decode)
        val violations = rules.violationsIn(dependencies)
        val staleExceptions = rules.staleExceptionsIn(dependencies)

        val report = buildString {
            appendLine("Checked ${dependencies.size} module dependencies.")
            violations.forEach { appendLine("VIOLATION  $it") }
            staleExceptions.forEach { appendLine("STALE EXCEPTION  ${it.source} -> ${it.dependency}  (${it.reason})") }
        }
        reportFile.get().asFile.writeText(report)

        if (violations.isNotEmpty() || staleExceptions.isNotEmpty()) {
            throw GradleException(
                "Architecture rules broken (config/architecture/rules.json):\n$report" +
                    "Fix the dependency, or remove an exception that no longer matches anything."
            )
        }
    }
}
