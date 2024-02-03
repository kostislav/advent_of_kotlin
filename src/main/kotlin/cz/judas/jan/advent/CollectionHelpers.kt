package cz.judas.jan.advent

import com.google.common.collect.BiMap
import com.google.common.collect.HashMultimap
import com.google.common.collect.HashMultiset
import com.google.common.collect.ImmutableBiMap
import com.google.common.collect.ImmutableMultiset
import com.google.common.collect.Iterables
import com.google.common.collect.Multimap
import com.google.common.collect.Multiset
import com.google.common.collect.Ordering
import org.apache.commons.math3.util.ArithmeticUtils
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

fun <T1, T2> List<T1>.cartesianProduct(other: List<T2>): Sequence<Pair<T1, T2>> {
    val otherAsSequence = other.asSequence()
    return this.asSequence().flatMap { item1 -> otherAsSequence.map { item2 -> Pair(item1, item2) } }
}

fun <T> List<T>.unorderedPairs(): Sequence<Pair<T, T>> {
    return asSequence()
        .flatMapIndexed { i, value -> subList(0, i).asSequence().map { value to it } }
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

fun <K, V> List<Pair<K, V>>.toMultiMap(): HashMultimap<K, V> {
    val result = HashMultimap.create<K, V>()
    for ((key, value) in this) {
        result.put(key, value)
    }
    return result
}

fun <K, VI, VO> Iterable<Pair<K, VI>>.toMergedMap(valuesForSameKeyTransform: (List<VI>) -> VO): Map<K, VO> {
    return groupBy({ it.first }, { it.second })
        .mapValues { valuesForSameKeyTransform(it.value) }
}

fun <T> Iterable<T>.toMultiSet(): Multiset<T> {
    return HashMultiset.create(this)
}

fun <T> Multiset<T>.asMap(): Map<T, Int> {
    return elementSet().associateWith { count(it) }
}

fun <T> multiSetOf(vararg values: T): Multiset<T> {
    return HashMultiset.create<T>(values.toList())
}

fun <T> emptyMultiSet(): Multiset<T> {
    return ImmutableMultiset.of()
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

fun <T1, T2, O> Pair<T1, T2>.mapFirst(transformation: (T1) -> O): Pair<O, T2> {
    return Pair(transformation(first), second)
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

fun <T, R> Iterable<T>.mapIndexedFrom(firstIndex: Int, transform: (index: Int, T) -> R): List<R> {
    return mapIndexed { i, item -> transform(i + firstIndex, item) }
}

fun <T> Iterable<T>.associateToIndex(startIndex: Int = 0): Map<T, Int> {
    return mapIndexedFrom(startIndex) { i, item -> item to i }.toMap()
}

fun <T : Any> unfold(initial: T, next: (T) -> T?): List<T> {
    val result = mutableListOf<T>()
    var current: T? = initial
    while (current !== null) {
        result += current
        current = next(current)
    }
    return result
}

fun <T> List<T>.replace(oldItem: T, newItem: T): List<T> {
    return map {
        if (it == oldItem) {
            newItem
        } else {
            it
        }
    }
}

fun <T> List<T>.cycle(): Sequence<T> {
    return generateSequence(this) { this }.flatten()
}

fun <T> Sequence<T>.cycle(): Sequence<T> {
    return generateSequence(this) { this }.flatten()
}

fun Iterable<Long>.leastCommonMultiple(): Long {
    val iterator = iterator()
    return iterator.fold(iterator.next()) { soFar, current -> ArithmeticUtils.lcm(soFar, current) }
}

operator fun <T> List<T>.times(count: Int): List<T> = List(count) { this }.flatten()

fun <T> Iterable<T>.sumOfInt(selector: (T) -> Int): Int {
    return sumOf(selector)
}

fun <T> Sequence<T>.takeWhileIndexed(predicate: (index: Int, T) -> Boolean): Sequence<T> {
    return mapIndexed { i, value -> Pair(i, value) }
        .takeWhile { predicate(it.first, it.second) }
        .map { it.second }
}

fun <K, V> Map<K, Set<V>>.inverse(): Map<V, Set<K>> {
    val result = mutableMapWithDefault<V, MutableSet<K>> { mutableSetOf() }
    forEach { (key, values) -> values.forEach { value -> result.getOrCreate(value) += key } }
    return result
}

fun <T> Iterable<T>.histogram(): Map<T, Int> {
    val result = mutableMapWithDefault<T, MutableInt> { MutableInt(0) }
    for (item in this) {
        result.getOrCreate(item).increment()
    }
    return result.mapValues { it.value.value }
}

operator fun <T> Multiset<T>.plus(other: Multiset<T>): Multiset<T> {
    val result = HashMultiset.create<T>(this)
    result.addAll(other)
    return result
}

fun <T, K> Iterable<T>.associateByMultiple(keySelector: (T) -> Iterable<K>): Map<K, T> {
    val destination = mutableMapOf<K, T>()
    for (element in this) {
        for (key in keySelector(element)) {
            destination.put(key, element)
        }
    }
    return destination
}

fun <T, O> Iterable<T>.translate(lookup: Map<T, O>): List<O> {
    return map { lookup.getValue(it) }
}

fun <T> Iterable<T>.pairWithIndex(startingIndex: Int = 0): List<Pair<Int, T>> {
    return mapIndexedFrom(startingIndex) { index, item -> index to item }
}

fun <T1, T2, R> Iterable<Pair<T1, T2>>.scanSecond(initial: R, operation: (acc: R, T2) -> R): List<Pair<T1, R>> {
    val result = mutableListOf<Pair<T1, R>>()
    var accumulator = initial
    for (element in this) {
        accumulator = operation(accumulator, element.second)
        result.add(Pair(element.first, accumulator))
    }
    return result
}

inline fun <T> Iterable<T>.partitionIndexed(predicate: (Int, T) -> Boolean): Pair<List<T>, List<T>> {
    return withIndex()
        .partition { predicate(it.index, it.value) }
        .map { part -> part.map { it.value } }
}

class LexicographicalListComparator<T>(
    private val itemComparator: Comparator<T>
) : Comparator<List<T>> {
    override fun compare(list1: List<T>, list2: List<T>): Int {
        for (i in list1.indices) {
            val itemResult = itemComparator.compare(list1[i], list2[i])
            if (itemResult != 0) {
                return itemResult
            }
        }
        return 0
    }

    companion object {
        fun <T : Comparable<T>> natural(): LexicographicalListComparator<T> {
            return LexicographicalListComparator(Comparator.naturalOrder())
        }
    }
}

class MutableMapWithDefault<K, V>(
    private val backing: MutableMap<K, V>,
    private val defaultValue: (K) -> V
) : MutableMap<K, V> by backing {
    fun getOrCreate(key: K): V {
        val value = get(key)
        return if (value === null) {
            val newValue = defaultValue(key)
            put(key, newValue)
            newValue
        } else {
            value
        }
    }

    override fun toString(): String {
        return backing.toString()
    }
}

fun <K, V> mutableMapWithDefault(defaultValue: (K) -> V): MutableMapWithDefault<K, V> {
    return MutableMapWithDefault(mutableMapOf(), defaultValue)
}

data class MutableInt(var value: Int) {
    fun increment() {
        value += 1
    }
}