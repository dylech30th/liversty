package me.lovemurasame.dylech30th

import me.lovemurasame.dylech30th.debugging.ILogger

class StdOutputComikazeLogger : ILogger {
    override fun err(content: String) {
        System.err.println("Comikaze: $content")
    }

    override fun errPrefixed(prefix: String, content: String) {
        System.err.println("${prefix}Comikaze: $content")
    }

    override fun log(content: String) {
        println("Comikaze: $content")
    }

    override fun logPrefixed(prefix: String, content: String) {
        println("${prefix}Comikaze: $content")
    }

    override fun toString(): String {
        return this::class.java.name
    }
}