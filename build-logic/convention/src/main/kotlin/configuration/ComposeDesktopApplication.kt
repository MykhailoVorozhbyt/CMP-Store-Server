package configuration


import extensions.composeExtension
import extensions.desktopExtension
import org.gradle.api.Project
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

fun Project.composeDesktopApplication(
    mainClass: String,
    packageName: String,
    version: String,
    targetFormats: List<TargetFormat> = listOf(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
) {
    composeExtension {
        desktopExtension {
            application {
                this.mainClass = mainClass

                nativeDistributions {
                    targetFormats(*targetFormats.toTypedArray())
                    this.packageName = packageName
                    this.packageVersion = version
                }

            }
        }
    }
}