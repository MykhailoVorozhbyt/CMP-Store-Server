package configuration


import extensions.desktopExtension
import org.gradle.api.Project
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

fun Project.composeDesktopApplication(
    mainClass: String,
    packageName: String,
    version: String,
    targetFormats: List<TargetFormat> = listOf(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
) {
    desktopExtension {
        application {
            this.mainClass = mainClass

            nativeDistributions {
                targetFormats(*targetFormats.toTypedArray())
                this.packageName = packageName
                this.packageVersion = version

                macOS {
//                    iconFile.set(project.file("src/commonMain/composeResources/drawable/app_icon.ico"))
                    iconFile.set(project.file("appicon/MacOsIc.icns"))
                    infoPlist {
                        extraKeysRawXml = macOsExtraKeysRawXml()
                    }
                }
                windows {
//                    iconFile.set(project.file("../media/appicon/icon_512.ico"))
                    msiPackageVersion = version
                    shortcut = true
                    dirChooser = true
                    menu = true
                }
                linux {
//                    iconFile.set(project.file("../media/appicon/icon_512.png"))
                    shortcut = true
                }
            }

            buildTypes.release.proguard {
                this.version.set("7.4.0")
                this.obfuscate.set(false)
                this.isEnabled.set(false)
            }
        }

    }
}

private fun macOsExtraKeysRawXml(): String = """
            <key>NSOutgoingConnectionsUsageDescription</key>
            <string>This app requires internet access to load content.</string>
            <key>NSAppTransportSecurity</key>
            <dict>
                <key>NSAllowsArbitraryLoads</key>
                <true/>
                <key>NSAllowsArbitraryLoadsInWebContent</key>
                <true/>
                <key>NSExceptionDomains</key>
                <dict>
                    <key>raw.githubusercontent.com</key>
                    <dict>
                        <key>NSIncludesSubdomains</key>
                        <true/>
                        <key>NSTemporaryExceptionAllowsInsecureHTTPLoads</key>
                        <true/>
                        <key>NSTemporaryExceptionMinimumTLSVersion</key>
                        <string>TLSv1.2</string>
                    </dict>
                </dict>
            </dict>
        """.trimIndent()