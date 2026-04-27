package utils

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val targetCompatibilityVersion = JavaVersion.VERSION_21
val sourceCompatibilityVersion = JavaVersion.VERSION_21
val currentJvmTarget = JvmTarget.JVM_21