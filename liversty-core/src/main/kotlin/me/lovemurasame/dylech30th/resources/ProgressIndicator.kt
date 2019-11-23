package me.lovemurasame.dylech30th.resources

import java.util.concurrent.atomic.AtomicLong

class ProgressIndicator(initialValue: Long = 0) {
    private val counter = AtomicLong(initialValue)

    fun updateProgressString(total: Long, prefix: String = "") {
        synchronized(this) {
            print("\r")
            print(buildProgressString(counter.incrementAndGet(), total, prefix))
        }
    }

    private fun buildProgressString(progress: Long, total: Long, prefix: String): String {
        val c = ((progress.toDouble() / total) * 50).toInt()
        return buildString {
            append(prefix)
            append('[')
            for (i in 0..c) {
                append('=')
            }
            append(">")
            for (i in 0..50 - c) {
                append(' ')
            }
            append(']')
            append("${((progress.toDouble() / total) * 100).toInt()}%")
        }
    }
}