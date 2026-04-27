package extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.kotlin.dsl.configure

internal fun KotlinMultiplatformExtension.androidLibrary(
    block: KotlinMultiplatformAndroidLibraryTarget.() -> Unit
) {
    configure<KotlinMultiplatformAndroidLibraryTarget>(block)
}