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

                    macOS {
                        iconFile.set(project.file("appicon/MacOsIc.icns"))
                        infoPlist {
                            extraKeysRawXml = """
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
                        }
                    }
                    //TODO: for te future
                    /*windows {
                        iconFile.set(project.file("icon.ico"))
                    }
                    linux {
                        iconFile.set(file("src/main/resources/splash_logo.png"))
                    }*/
                }
            }

        }
    }
}