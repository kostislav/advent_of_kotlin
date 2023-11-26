package cz.judas.jan.advent

import com.google.common.collect.Ordering
import java.io.Reader
import java.io.StringReader
import java.nio.charset.StandardCharsets

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

class InputData(private val source: () -> Reader) {
    fun lines(): List<String> {
        return source()
            .useLines { it.toList() }
    }

    companion object {
        fun forDay(year: Int, day: Int): InputData {
            val resource = InputData::class.java.getResource("year${year}/day${day}")!!
            return InputData {
                resource.openStream()
                    .reader(StandardCharsets.UTF_8)
            }
        }

        fun fromString(contents: String): InputData {
            return InputData { StringReader(contents) }
        }
    }
}