package org.libertycat.kmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform