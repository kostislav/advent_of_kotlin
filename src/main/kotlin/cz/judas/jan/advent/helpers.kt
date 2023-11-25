package cz.judas.jan.advent

import com.google.common.collect.Ordering
import java.io.InputStream
import java.nio.charset.StandardCharsets

fun <T : Comparable<T>> List<T>.maxN(howMany: Int): List<T> {
    return Ordering.natural<T>().greatestOf(this, howMany)
}

fun <T> List<T>.splitOn(predicate: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<List<T>>()
    val current = mutableListOf<T>()

    for (item in this) {
        if (predicate(item)) {
            if (!current.isEmpty()) {
                result += current.toList()
            }
            current.clear()
        } else {
            current += item
        }
    }

    return result
}

class InputData(private val source: () -> InputStream) {
    fun lines(): List<String> {
        return source()
            .reader(StandardCharsets.UTF_8)
            .useLines { it.toList() }
    }

    companion object {
        fun forDay(day: Int): InputData {
            val resource = InputData::class.java.getResource("day${day}")!!
            return InputData { resource.openStream() }
        }
    }
}