package me.lovemurasame.dylech30th.config

import me.lovemurasame.dylech30th.resources.Registry
import me.lovemurasame.dylech30th.resources.checkAndThrow
import me.lovemurasame.dylech30th.resources.openCreate
import java.io.Closeable
import java.io.File
import java.io.FileWriter
import java.io.Flushable
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.StringBuilder

class InitializationManager : Closeable, IPrimitiveWritable, Flushable {
    private var fileSession: File? = null
    private val keyValuePairs = mutableMapOf<String, String>()

    companion object Internal {
        private val fileMapping = mutableMapOf<String, String>()

        fun addFileMapping(key: String, file: String) = fileMapping.putIfAbsent(key, file)

        fun addFileMapping(reg: Registry) = addFileMapping(reg.key, reg.value)

        fun removeFileMapping(key: String) = fileMapping.remove(key)
    }

    private fun assertSessionStart() {
        if (fileSession == null) {
            throw IllegalStateException()
        }
    }

    fun open(key: String) {
        assertConfigRegistered(key)
        assertFileSessionNotStarted()

        fileSession = openCreate(fileMapping[key]!!)
        initKeyValues()
    }

    private fun assertFileSessionNotStarted() {
        if (fileSession != null) {
            throw IllegalStateException()
        }
    }

    private fun assertConfigRegistered(key: String) {
        if (!fileMapping.containsKey(key)) {
            throw KeyNotFoundException()
        }
    }

    private fun initKeyValues() {
        assertSessionStart()
        this.fileSession!!.readLines()
                .forEach { x -> with(x.split("=")) { keyValuePairs[this[0]] = this[1] } }
    }

    override fun getChar(key: String): Char {
        ensureKeyRegistered(key)
        return keyValuePairs[key]!!.single()
    }

    override fun getInt(key: String): Int {
        ensureKeyRegistered(key)
        return keyValuePairs[key]!!.toInt()
    }

    override fun getLong(key: String): Long {
        ensureKeyRegistered(key)
        return keyValuePairs[key]!!.toLong()
    }

    override fun getString(key: String): String {
        ensureKeyRegistered(key)
        return keyValuePairs[key]!!
    }

    override fun getBoolean(key: String): Boolean {
        ensureKeyRegistered(key)
        return keyValuePairs[key]!!.toBoolean()
    }

    override fun getDouble(key: String): Double {
        ensureKeyRegistered(key)
        return keyValuePairs[key]!!.toDouble()
    }

    override fun writeChar(key: String, value: Char) {
        writeObject(key, value)
    }

    override fun writeInt(key: String, value: Int) {
        writeObject(key, value)
    }

    override fun writeLong(key: String, value: Long) {
        writeObject(key, value)
    }

    override fun writeString(key: String, value: String) {
        writeObject(key, value)
    }

    override fun writeBoolean(key: String, value: Boolean) {
        writeObject(key, value)
    }

    override fun writeDouble(key: String, value: Double) {
        writeObject(key, value)
    }

    private fun writeObject(key: String, value: Any) {
        keyValuePairs[key] = value.toString()
    }

    private fun ensureKeyRegistered(key: String) {
        checkAndThrow<MutableMap<String, String>, KeyNotFoundException>(keyValuePairs)
        { containsKey(key) && this[key] != null }
    }

    override fun flush() {
        FileWriter(fileSession!!, false).use {
            it.write(buildString())
        }
    }

    private fun buildString(): String {
        return with(StringBuilder()) {
            keyValuePairs.forEach { (k, v) ->
                appendln("$k=$v")
            }
            this.toString()
        }
    }

    override fun close() {
        flush()
    }

    override fun tryGetDefault(key: String, value: Any): Any {
        if (keyValuePairs.containsKey(key)) {
            return keyValuePairs[key]!!
        }
        writeObject(key, value)
        return keyValuePairs[key]!!
    }

    override fun tryGetWithEntryDefault(reg: Registry): Any {
        return tryGetDefault(reg.key, reg.value)
    }

    inner class KeyNotFoundException : Exception {
        constructor() : super()
        constructor(message: String?) : super(message)
        constructor(message: String?, cause: Throwable?) : super(message, cause)
        constructor(cause: Throwable?) : super(cause)
        constructor(
                message: String?,
                cause: Throwable?,
                enableSuppression: Boolean,
                writableStackTrace: Boolean
        ) : super(message, cause, enableSuppression, writableStackTrace)
    }
}