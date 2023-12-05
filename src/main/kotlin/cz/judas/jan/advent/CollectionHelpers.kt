package cz.judas.jan.advent

import com.google.common.collect.BiMap
import com.google.common.collect.HashMultimap
import com.google.common.collect.HashMultiset
import com.google.common.collect.ImmutableBiMap
import com.google.common.collect.Iterables
import com.google.common.collect.Multimap
import com.google.common.collect.Multiset
import com.google.common.collect.Ordering
import cz.judas.jan.advent.year2023.Day5
import java.util.*


fun <T : Comparable<T>> List<T>.maxN(howMany: Int): List<T> {
    return Ordering.natural<T>().greatestOf(this, howMany)
}

fun <T> List<T>.splitOn(predicate: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<List<T>>()
    val current = mutableListOf<T>()

    for (item in this) {
        if (predicate(item)) {
            if (current.isNotEmpty()) {
                result += current.toList()
            }
            current.clear()
        } else {
            current += item
        }
    }

    if (current.isNotEmpty()) {
        result += current.toList()
    }

    return result
}

fun <T1, T2> List<T1>.cartesianProduct(other: List<T2>): List<Pair<T1, T2>> {
    return this.flatMap { item1 -> other.map { item2 -> Pair(item1, item2) } }
}

fun <T> List<T>.splitOnOnly(delimiter: (T) -> Boolean): Pair<List<T>, List<T>> {
    val parts = splitOn(delimiter)
    if (parts.size == 2) {
        return Pair(parts[0], parts[1])
    } else {
        throw RuntimeException("Could not split")
    }
}

fun <T> List<T>.subList(fromIndex: Int): List<T> {
    return subList(fromIndex, size)
}

fun <T> List<T>.trimEnd(whatToRemove: (T) -> Boolean): List<T> {
    val result = toMutableList()
    for (i in size downTo 1) {
        if (whatToRemove(result[i - 1])) {
            result.removeLast()
        } else {
            break
        }
    }
    return result
}

fun <T> List<T>.trimEnd(whatToRemove: T): List<T> {
    return trimEnd { it == whatToRemove }
}

fun <T> List<T>.pickByIndex(vararg indexes: Int): List<T> {
    return indexes.map { get((it + size) % size) }
}

fun <K : Comparable<K>, V> List<Pair<K, V>>.toNavigableMap(): NavigableMap<K, V> {
    return TreeMap(toMap())
}

fun <K : Comparable<K>, V> List<Pair<K, V>>.toMultiMap(): Multimap<K, V> {
    val result = HashMultimap.create<K, V>()
    for ((key, value) in this) {
        result.put(key, value)
    }
    return result
}

fun <K : Any, V : Any> biMapOf(vararg pairs: Pair<K, V>): BiMap<K, V> {
    return pairs.fold(ImmutableBiMap.builder<K, V>()) { builder, (key, value) ->
        builder.put(key, value)
    }.build()
}

fun <K, VI, VO> Multimap<K, VI>.toMap(combiner: (Iterable<VI>) -> VO): Map<K, VO> {
    return buildMap {
        asMap().forEach { (key, values) -> put(key, combiner(values)) }
    }
}

fun <I, O> Pair<I, I>.map(transformation: (I) -> O): Pair<O, O> {
    return Pair(transformation(first), transformation(second))
}

fun <I1, I2, O> Pair<I1, I2>.collect(merger: (I1, I2) -> O): O {
    return merger(first, second)
}

fun <T> Iterable<T>.getOnlyElement(): T {
    return Iterables.getOnlyElement(this)
}

fun <I, O> Iterator<I>.fold(initial: O, operation: (O, I) -> O): O {
    var current = initial
    while (hasNext()) {
        current = operation(current, next())
    }
    return current
}

fun <T> Iterable<Set<T>>.intersection(): Set<T> {
    val iterator = iterator()
    return if (iterator.hasNext()) {
        iterator.fold(iterator.next(), Set<T>::intersect)
    } else {
        emptySet()
    }
}

fun Iterable<Int>.product(): Int {
    return fold(1) { acc, value -> acc * value }
}

fun Iterable<Long>.product(): Long {
    return fold(1L) { acc, value -> acc * value }
}

fun <K, V> SortedMap<K, V>.firstValue(): V {
    return getValue(firstKey())
}

fun <K, V> SortedMap<K, V>.lastValue(): V {
    return getValue(lastKey())
}

fun <T> mutableMultisetOf(): HashMultiset<T> {
    return HashMultiset.create()
}

operator fun <T> Multiset<T>.plusAssign(item: T) {
    add(item)
}

operator fun <T> Multiset<T>.minusAssign(item: T) {
    remove(item)
}

fun <T: Any> unfold(initial: T, next: (T) -> T?): List<T> {
    val result = mutableListOf<T>()
    var current: T? = initial
    while (current !== null) {
        result += current
        current = next(current)
    }
    return result
}