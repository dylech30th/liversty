package me.lovemurasame.dylech30th.debugging

import me.lovemurasame.dylech30th.resources.ternary
import kotlin.contracts.ExperimentalContracts

interface ILogger {
    companion object {
        fun createLogger(name: String): ILogger {
            return Class.forName(name).newInstance() as ILogger
        }
    }

    @ExperimentalContracts
    fun log(content: Any, map: ((Any) -> String)?) = ternary(map == null, log(content.toString()), log(map!!(content)))

    fun log(content: String)

    fun err(content: String)

    fun errPrefixed(prefix: String, content: String)

    fun logPrefixed(prefix: String, content: String) = log("$prefix$content")
}