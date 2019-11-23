package me.lovemurasame.dylech30th.resources

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.Closeable
import javax.script.ScriptEngineManager
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun <T : Closeable?, R> use(instance: T, block: T.() -> R): R = instance.use(block)

@ExperimentalContracts
fun <T> ternary(cond: Boolean, ifTrue: T, ifFalse: T): T {
    contract {
        returns() implies(ifTrue != null && ifFalse != null)
    }
    return if(cond) ifTrue else ifFalse
}

fun <T, R> ThreadLocal<T>.value(block: T.() -> R): R = block(this.get())

fun String.builder(): StringBuilder = StringBuilder(this)

inline fun <reified T> String.fromJson(): T = Gson().fromJson(this, T::class.java)

inline fun <reified T> T.toJson(): String = GsonBuilder().setPrettyPrinting().create().toJson(this, T::class.java)

fun throwIf(prediction: Boolean, clazz: Class<Throwable>) {
    if (prediction) throw clazz.newInstance()
}

infix fun String.compose(str: String): String = this + str

fun registryOf(key: String, value: String): Registry = Registry(key, value)

fun registryOf(key: Any, value: Any): Registry = registryOf(key.toString(), value.toString())

inline fun <T, reified E : Throwable> checkAndThrow(instance: T, predicate: T.() -> Boolean) {
    if (predicate(instance)) {
        throw E::class.java.newInstance()
    }
}

inline fun <T> withAll(vararg obj: T, block: T.() -> Unit) {
    for (o in obj) {
        block(o)
    }
}

fun String.eval(): String {
    return with(ScriptEngineManager().getEngineByName("javascript")) {
        eval(this@eval).toString()
    }
}