package extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

//TODO: use form agb9 - androidLibrary
internal fun KotlinMultiplatformExtension.androidLibrary(
    block: KotlinMultiplatformAndroidLibraryTarget.() -> Unit
) {
    configure<KotlinMultiplatformAndroidLibraryTarget>(block)
}