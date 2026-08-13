package com.feature.authentication.data.di

import java.io.File
import java.util.Properties

internal class DesktopClientSecretResolver {
    /**
     * Resolves the Google Desktop OAuth client secret at runtime so it is never compiled
     * into the binary or committed to source control. For local dev the value lives in the
     * gitignored secrets.properties (repo root); on CI/prod it comes from an environment variable.
     */
    fun resolveDesktopClientSecret(): String =
        System.getenv(DESKTOP_CLIENT_SECRET_ENV)?.takeIf { it.isNotBlank() }
            ?: readSecretFromProperties()
            ?: error(
                "Missing $DESKTOP_CLIENT_SECRET_ENV. Set it as an environment variable " +
                        "or add it to secrets.properties at the repo root."
            )

    private fun readSecretFromProperties(): String? {
        val file = findSecretsFile() ?: return null
        val properties = Properties().apply { file.inputStream().use(::load) }
        return properties.getProperty(DESKTOP_CLIENT_SECRET_ENV)
            ?.trim()
            ?.trim('"')
            ?.takeIf { it.isNotBlank() }
    }

    /** Walks up from the runtime working directory so the file is found whether the app is
     * launched from a store module dir or the repo root. */
    private fun findSecretsFile(): File? =
        generateSequence(File(System.getProperty("user.dir"))) { it.parentFile }
            .map { File(it, "secrets.properties") }
            .firstOrNull(File::exists)

    companion object {
        private const val DESKTOP_CLIENT_SECRET_ENV = "DESKTOP_CLIENT_SECRET"
    }
}
