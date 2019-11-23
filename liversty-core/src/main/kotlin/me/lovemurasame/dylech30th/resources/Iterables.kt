package me.lovemurasame.dylech30th.resources

import com.google.common.collect.ImmutableList
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.random.Random

fun <K, V> MutableMap<K, V>.put(pair: Pair<K, V>) {
    this[pair.first] = pair.second
}

fun <T> List<T>.pickOne() = get(Random.nextInt(0, this.size))

fun <T> List<T>.sequenceEqual(another: List<T>): Boolean {
    if (this.size != another.size)
        return false

    for (e in this) {
        if (!another.any { it == e })
            return false
    }
    return true
}

fun <T, R> List<T>.sequenceEqual(another: List<T>, keySelector: (T) -> R): Boolean {
    return map(keySelector).sequenceEqual(another.map(keySelector))
}

fun List<*>.sizeEquals(another: List<*>): Boolean {
    return size == another.size
}

fun <T> T.mappedEquals(another: T, mapper: (T) -> Any): Boolean {
    return Objects.equals(mapper(this), mapper(another))
}

fun <T> Collection<T>.toCopyOnWriteArraySet(): CopyOnWriteArraySet<T> {
    return CopyOnWriteArraySet(this)
}

fun <T> Collection<T>.toCopyOnWriteArrayList(): CopyOnWriteArrayList<T> {
    return CopyOnWriteArrayList(this)
}

fun <K, V, A> Map<K, V>.valuesMap(mapping: (V) -> A): MutableMap<K, A> {
    val map = mutableMapOf<K, A>()
    for ((k, v) in this) {
        map[k] = mapping(v)
    }
    return map
}

fun <E> immutableListOf(e: MutableCollection<E>): ImmutableList<E> {
    return ImmutableList.copyOf(e)
}

fun <E> immutableListOf(vararg e: E): ImmutableList<E> {
    return ImmutableList.copyOf(e)
}

inline fun <reified E> Collection<E>.toImmutable(): ImmutableList<E> {
    return immutableListOf(*this.toTypedArray())
}

fun <E> Collection<E>.reducedTo(toCompared: Collection<E>): Boolean {
    return this.size < toCompared.size
}

operator fun <K, PK, PV> Map.Entry<K, Pair<PK, PV>>.component2(): PK {
    return this.value.first
}

operator fun <K, PK, PV> Map.Entry<K, Pair<PK, PV>>.component3(): PV {
    return this.value.second
}