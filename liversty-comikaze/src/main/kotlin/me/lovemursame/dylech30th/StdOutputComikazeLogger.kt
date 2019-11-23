package me.lovemursame.dylech30th

import me.lovemurasame.dylech30th.debugging.ILogger

class StdOutputComikazeLogger : ILogger {
    override fun log(content: String) {
        println("Comikaze: $content")
    }

    override fun logPrefixed(prefix: String, content: String) {
        println("\nComikaze: $content")
    }

    override fun toString(): String {
        return this::class.java.name
    }
}