package cz.judas.jan.advent

import com.google.common.collect.BiMap
import com.google.common.collect.HashMultimap
import com.google.common.collect.HashMultiset
import com.google.common.collect.ImmutableBiMap
import com.google.common.collect.Iterables
import com.google.common.collect.Multimap
import com.google.common.collect.Multiset
import com.google.common.collect.Ordering
import org.ahocorasick.trie.Trie
import org.apache.http.HttpMessage
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import java.io.Reader
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

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

fun <K : Any, V : Any> biMapOf(vararg pairs: Pair<K, V>): BiMap<K, V> {
    return pairs.fold(ImmutableBiMap.builder<K, V>()) { builder, (key, value) ->
        builder.put(key, value)
    }.build()
}

fun String.splitInHalf(): Pair<String, String> {
    if (length % 2 == 0) {
        val half = length / 2
        return Pair(substring(0, half), substring(half))
    } else {
        throw RuntimeException("String has odd length: ${this}")
    }
}

fun String.characters(): List<Char> {
    return toCharArray().toList()
}

fun String.splitOnOnly(delimiter: String): Pair<String, String> {
    val parts = split(delimiter)
    if (parts.size == 2) {
        return Pair(parts[0], parts[1])
    } else {
        throw RuntimeException("Expected a single ${delimiter} delimiter in ${this}")
    }
}

fun String.pickByIndex(vararg indexes: Int): List<Char> {
    return indexes.map { get((it + length) % length) }
}

fun String.findAll(items: Set<String>, overlapping: Boolean): List<Pair<String, Int>> {
    val builder = Trie.builder()
    if (!overlapping) {
        builder.ignoreOverlaps()
    }
    val trie = items.fold(builder, Trie.TrieBuilder::addKeyword).build()
    return trie.parseText(this).map { emit -> Pair(emit.keyword, emit.start) }
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

fun <K : Comparable<K>, V> List<Pair<K, V>>.toSortedMap(): SortedMap<K, V> {
    return toMap().toSortedMap()
}

fun <K : Comparable<K>, V> List<Pair<K, V>>.toMultiMap(): Multimap<K, V> {
    val result = HashMultimap.create<K, V>()
    for ((key, value) in this) {
        result.put(key, value)
    }
    return result
}

fun <K, VI, VO> Multimap<K, VI>.toMap(combiner: (Iterable<VI>) -> VO): Map<K, VO> {
    return buildMap {
        asMap().forEach{ (key, values) -> put(key, combiner(values))}
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

class TwoDimensionalArray<out T> private constructor(private val items: List<List<T>>) {
    val numRows get() = items.size

    val numColumns get() = items[0].size

    operator fun get(x: Int, y: Int): T = items[x][y]

    fun rotateRight(): TwoDimensionalArray<T> {
        return create(numColumns, numRows) { i, j -> get(numRows - 1 - j, i) }
    }

    fun row(index: Int): List<T> {
        return items[index]
    }

    companion object {
        fun charsFromLines(lines: List<String>): TwoDimensionalArray<Char> {
            return create(lines.size, lines[0].length) { i, j -> lines[i][j] }
        }

        fun <T> create(numRows: Int, numColumns: Int, initializer: (Int, Int) -> T): TwoDimensionalArray<T> {
            return TwoDimensionalArray(List(numRows) { i ->
                List(numColumns) { j ->
                    initializer(i, j)
                }.toList()
            }.toList())
        }
    }
}

class InputData(private val source: () -> Reader) {
    fun lines(): List<String> {
        return source()
            .useLines { it.toList() }
    }

    fun asString(): String {
        return source().readText()
    }

    companion object {
        fun fromString(contents: String): InputData {
            return InputData { StringReader(contents) }
        }
    }
}

class InputFetcher {
    fun get(year: Int, day: Int): InputData {
        val relativeResourcePath = "year${year}/day${day}"
        val resource = InputData::class.java.getResource(relativeResourcePath)
        return if (resource === null) {
            val data = fetch(year, day)
            Path("src/main/resources/cz/judas/jan/advent/${relativeResourcePath}").writeText(
                data,
                StandardCharsets.UTF_8
            )
            InputData { StringReader(data) }
        } else {
            InputData {
                resource.openStream().reader(StandardCharsets.UTF_8)
            }
        }
    }

    private fun fetch(year: Int, day: Int): String {
        return AdventOfCodeApi().getPuzzleInput(year, day)
    }
}

class AdventOfCodeApi {
    private val httpClient = HttpClients.createDefault()

    fun getPuzzleInput(year: Int, day: Int): String {
        val request = HttpGet("https://adventofcode.com/${year}/day/${day}/input").apply {
            addHeaders()
        }

        return httpClient.execute(request).use { it.entity.content.reader().readText().trimEnd('\n') }
    }

    fun submitAnswer(year: Int, day: Int, part: Int, answer: Any) {
        val request = HttpPost("https://adventofcode.com/${year}/day/${day}/answer").apply {
            addHeaders()
            entity = UrlEncodedFormEntity(
                listOf(
                    BasicNameValuePair("level", part.toString()),
                    BasicNameValuePair("answer", answer.toString()),
                )
            )
        }

        httpClient.execute(request).close()
    }

    private fun HttpMessage.addHeaders() {
        setHeader("Cookie", "session=${getSession()}")
        setHeader("User-Agent", "https://github.com/kostislav/advent_of_kotlin by snugar.i@gmail.com")
    }

    private fun getSession() = Path(".session").readText()
}

data class Vector2d(val x: Int, val y: Int)

enum class Digit {
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE;

    val value: Int get() = ordinal
}