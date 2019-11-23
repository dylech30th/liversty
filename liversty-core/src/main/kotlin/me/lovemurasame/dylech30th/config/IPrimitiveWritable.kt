package me.lovemurasame.dylech30th.config

import me.lovemurasame.dylech30th.resources.Registry

interface IPrimitiveWritable {
    fun getChar(key: String): Char

    fun getInt(key: String): Int

    fun getLong(key: String): Long

    fun getString(key: String): String

    fun getBoolean(key: String): Boolean

    fun getDouble(key: String): Double

    fun writeChar(key: String, value: Char)

    fun writeInt(key: String, value: Int)

    fun writeLong(key: String, value: Long)

    fun writeString(key: String, value: String)

    fun writeBoolean(key: String, value: Boolean)

    fun writeDouble(key: String, value: Double)

    fun tryGetDefault(key: String, value: Any): Any

    fun tryGetWithEntryDefault(reg: Registry): Any
}