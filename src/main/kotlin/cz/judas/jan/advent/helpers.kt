package cz.judas.jan.advent

import kotlin.math.ceil
import kotlin.math.floor

fun Double.nextSmallerInt(): Int {
    val floor = floor(this)
    return if (floor == this) {
        floor - 1
    } else {
        floor
    }.toInt()
}

fun Double.nextBiggerInt(): Int {
    val ceil = ceil(this)
    return if (ceil == this) {
        ceil + 1
    } else {
        ceil
    }.toInt()
}


class TwoDimensionalArray<out T> private constructor(private val items: List<List<T>>) {
    val numRows get() = items.size

    val numColumns get() = items[0].size

    operator fun get(x: Int, y: Int): T = items[x][y]

    fun getOrNull(x: Int, y: Int): T? {
        return if (x in 0..<numRows && y in 0..<numColumns) {
            get(x, y)
        } else {
            null
        }
    }

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

@Target(AnnotationTarget.FUNCTION)
annotation class Answer(val value: String)