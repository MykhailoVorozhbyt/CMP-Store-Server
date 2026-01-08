package configuration

import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import utils.currentJvmTarget

/**
 * Configure base Kotlin options
 * */
internal inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() =
    configure<T> {
        when (this) {
            is KotlinAndroidProjectExtension -> compilerOptions
            is KotlinJvmProjectExtension -> compilerOptions
            else -> throw NotImplementedError("Unsupported project extension $this ${T::class}")
        }.apply {
            jvmTarget = currentJvmTarget
        }
    }