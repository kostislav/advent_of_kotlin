package cz.judas.jan.advent

import com.google.common.collect.BiMap
import com.google.common.collect.ImmutableBiMap
import com.google.common.collect.Iterables
import com.google.common.collect.Ordering
import java.io.Reader
import java.io.StringReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.charset.StandardCharsets
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
            Path("src/main/resources/cz/judas/jan/advent/${relativeResourcePath}").writeText(data, StandardCharsets.UTF_8)
            InputData { StringReader(data) }
        } else {
            InputData {
                resource.openStream().reader(StandardCharsets.UTF_8)
            }
        }
    }

    private fun fetch(year: Int, day: Int): String {
        val session = Path(".session").readText()
        val request = HttpRequest.newBuilder()
            .uri(URI("https://adventofcode.com/${year}/day/${day}/input"))
            .header("Cookie", "session=${session}")
            .GET()
            .build()

        val httpClient = HttpClient.newHttpClient()

        return httpClient.send(request, BodyHandlers.ofString()).body().trimEnd('\n')
    }
}