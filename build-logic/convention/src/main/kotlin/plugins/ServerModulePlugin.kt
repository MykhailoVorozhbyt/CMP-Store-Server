package plugins

import extensions.alias
import extensions.implementation
import extensions.libs
import extensions.module
import extensions.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.plugins.JavaApplication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import utils.enums.ModuleName
import utils.enums.ModulePath

class ServerModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.alias(libs.plugins.kotlinJvm)
        pluginManager.alias(libs.plugins.ktor)
        // @Resource classes are @MetaSerializable — without this plugin the server
        // compiles but fails at startup with "Serializer for class ... not found".
        pluginManager.alias(libs.plugins.serialization)

        group = ModuleName.APP.mName
        version = "1.0.0"
        extensions.configure<JavaApplication> {
            mainClass.set("org.cmp.store.ApplicationKt")
            extensions.configure<ExtraPropertiesExtension> {
                val isDevelopment: Boolean = has("development")
                applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
            }
        }

        dependencies {
            module(ModulePath.SHARED)
            module(ModulePath.CORE_UTILS)
            implementation(libs.logback)
            implementation(libs.koin.ktor)
            implementation(libs.ktor.serverCore)
            implementation(libs.ktor.serverNetty)
            implementation(libs.ktor.serverContentNegotiation)
            implementation(libs.ktor.serverStatusPages)
            implementation(libs.ktor.serverResources)
            implementation(libs.ktor.serverAuth)
            implementation(libs.ktor.serverRateLimit)
            implementation(libs.ktor.serverDoubleReceive)
            implementation(libs.ktor.serverForwardedHeader)
            implementation(libs.ktor.serializationKotlinxJson)
            implementation(libs.exposed.core)
            implementation(libs.exposed.dao)
            implementation(libs.exposed.jdbc)
            implementation(libs.sqlite.jdbc)
            testImplementation(libs.ktor.serverTestHost)
            testImplementation(libs.kotlin.testJunit)
            testImplementation(libs.koin.test)
        }
    }
}