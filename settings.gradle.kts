rootProject.name = "CMP-Store-Server"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
include(":composeApp")
include(":server")
include(":shared")
include(":stores:athletica-plus")
include(":stores:athletica-plus:androidApp")
include(":stores:nutri-sport")
include(":stores:nutri-sport:androidApp")
include(":di")
include(":core:data")
include(":core:domain")
include(":core:presentation")
include(":core:utils")
include(":core:resources")
include(":core:navigation")
include(":core:security")
include(":core:network")
include(":feature:authentication")
include(":feature:home")
include(":component:customer:model")
include(":component:customer:domain-api")
include(":component:customer:usecase")
include(":component:customer:data")
include(":component:product:model")
include(":component:product:domain-api")
include(":component:product:usecase")
include(":component:product:data")
include(":component:auth:model")
include(":component:auth:domain-api")
include(":component:auth:usecase")
include(":component:auth:data")
include(":test")
