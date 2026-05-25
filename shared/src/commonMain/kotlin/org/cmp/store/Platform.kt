package org.cmp.store

interface Platform {
    val name: String
    val type: PlatformType

    val isMobile: Boolean
        get() = type.isMobile()

    val isDesktop: Boolean
        get() = type.isDesktop()
}

enum class PlatformType {
    ANDROID, IOS, JVM;

    fun isMobile() = this == ANDROID || this == IOS
    fun isDesktop() = this == JVM
}

expect fun getPlatform(): Platform