package architecture

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

private const val SESSION_SCOPED_STORAGE = "SessionScopedStorage"
private const val COMMENT_PREFIX = "#"
private const val REASON_SEPARATOR = "—"

private val storeInterface = Regex("""interface\s+(\w+(?:LocalDataSource|SessionStorage))\b([^{]*)""")

@CacheableTask
abstract class ValidateSessionScopedStoresTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: ConfigurableFileCollection

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val deviceScopedStoresFile: RegularFileProperty

    @get:OutputFile
    abstract val reportFile: RegularFileProperty

    @TaskAction
    fun validate() {
        val listPath = deviceScopedStoresFile.get().asFile.name
        val deviceScoped = parseDeviceScopedStores()
        val stores = sources.files
            .flatMap { file -> storeInterface.findAll(file.readText()).map { it.toStore() } }
            .associateBy(Store::name)
        val unlisted = stores.values
            .filterNot { it.isSessionScoped || it.name in deviceScoped }
            .map(Store::name)
            .sorted()
        val stale = (deviceScoped - stores.keys).sorted()
        val sessionScopedCount = stores.values.count(Store::isSessionScoped)

        val report = buildString {
            appendLine("Checked ${stores.size} stores: $sessionScopedCount session-scoped, ${deviceScoped.size} device-scoped.")
            unlisted.forEach { appendLine("NOT CLEARED ON SIGN-OUT  $it") }
            stale.forEach { appendLine("STALE DEVICE-SCOPED ENTRY  $it") }
        }
        reportFile.get().asFile.writeText(report)

        val errors = buildList {
            if (unlisted.isNotEmpty()) {
                add(
                    "These stores must implement $SESSION_SCOPED_STORAGE (and bind it next to their own binding), " +
                        "or be listed in $listPath with a reason:\n" + unlisted.joinToString("\n") { "  - $it" }
                )
            }
            if (stale.isNotEmpty()) {
                add("$listPath lists stores that are gone — remove these lines:\n" + stale.joinToString("\n") { "  - $it" })
            }
        }
        if (errors.isNotEmpty()) {
            throw GradleException(errors.joinToString("\n\n"))
        }
        logger.lifecycle(report.trimEnd())
    }

    private fun parseDeviceScopedStores(): Set<String> =
        deviceScopedStoresFile.get().asFile.readLines()
            .map(String::trim)
            .filterNot { it.isEmpty() || it.startsWith(COMMENT_PREFIX) }
            .map { line ->
                val name = line.substringBefore(REASON_SEPARATOR).trim()
                val reason = line.substringAfter(REASON_SEPARATOR, missingDelimiterValue = "").trim()
                if (reason.isEmpty()) {
                    throw GradleException("Device-scoped store '$name' needs a reason: '$name $REASON_SEPARATOR <why it survives sign-out>'")
                }
                name
            }
            .toSet()

    private fun MatchResult.toStore() = Store(
        name = groupValues[1],
        isSessionScoped = SESSION_SCOPED_STORAGE in groupValues[2],
    )

    private data class Store(val name: String, val isSessionScoped: Boolean)
}
