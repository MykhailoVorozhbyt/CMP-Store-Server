package org.cmp.store

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform