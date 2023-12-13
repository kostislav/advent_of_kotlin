package cz.judas.jan.advent

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor


fun Range<Double>.enclosedLongRange(): Range<Long> {
    val ceilStart = ceil(lowerEndpoint())
    val floorEnd = floor(upperEndpoint())

    return Range.closed(
        ceilStart.toLong() + if (hasLowerBound() && lowerBoundType() == BoundType.OPEN && ceilStart == lowerEndpoint()) 1 else 0,
        floorEnd.toLong() - if (hasUpperBound() && upperBoundType() == BoundType.OPEN && floorEnd == upperEndpoint()) 1 else 0
    )
}


fun Range<Long>.length(): Long {
    return upperEndpoint() - lowerEndpoint() + 1
}


fun <I1, I2, O> recursive(input1: I1, input2: I2, cached: Boolean = false, calculation: (I1, I2, (I1, I2) -> O) -> O): O {
    return recursive(Pair(input1, input2), cached) { (next1, next2), recursion ->
        calculation(next1, next2) { rec1, rec2 ->
            recursion(Pair(rec1, rec2))
        }
    }
}

fun <I, O> recursive(input: I, cached: Boolean = false, calculation: (I, (I) -> O) -> O): O {
    val cache = mutableMapOf<I, O>()
    val recursiveHolder = AtomicReference<(I) -> O>()
    if (cached) {
        recursiveHolder.set { key ->
            val cachedValue = cache.get(key)
            if (cachedValue === null) {
                val computedValue = calculation(key, recursiveHolder.get())
                cache.put(key, computedValue)
                computedValue
            } else {
                cachedValue
            }
        }
    } else {
        recursiveHolder.set { key -> calculation(key, recursiveHolder.get()) }
    }
    return calculation(input, recursiveHolder.get())
}


class TwoDimensionalArray<out T>(
    val numRows: Int,
    val numColumns: Int,
    private val lookup: (Int, Int) -> T
) {
    operator fun get(x: Int, y: Int): T = lookup(x, y)

    operator fun get(position: Coordinate): T = get(position.row, position.column)

    fun getOrNull(x: Int, y: Int): T? {
        return if (x in 0..<numRows && y in 0..<numColumns) {
            get(x, y)
        } else {
            null
        }
    }

    fun getOrNull(position: Coordinate): T? {
        return getOrNull(position.row, position.column)
    }

    fun rotateRight(): TwoDimensionalArray<T> {
        return TwoDimensionalArray(numColumns, numRows) { row, column -> get(numRows - 1 - column, row) }
    }

    fun transpose(): TwoDimensionalArray<T> {
        return TwoDimensionalArray(numColumns, numRows) { row, column -> get(column, row)}
    }

    fun row(index: Int): Sequence<T> {
        return columnIndices()
            .asSequence()
            .map { columnIndex -> get(index, columnIndex) }
    }

    fun column(index: Int): Sequence<T> {
        return rowIndices()
            .asSequence()
            .map { rowIndex -> get(rowIndex, index) }
    }

    fun <O> map(transformation: (T) -> O): TwoDimensionalArray<O> {
        return TwoDimensionalArray(numRows, numColumns) { row, column -> transformation(get(row, column))}
    }

    fun <O> mapIndexed(transformation: (row: Int, column: Int, T) -> O): TwoDimensionalArray<O> {
        return TwoDimensionalArray(numRows, numColumns) { row, column -> transformation(row, column, get(row, column))}
    }

    fun first(predicate: (T) -> Boolean): Coordinate {
        return (0..<numRows).asSequence()
            .flatMap { row -> (0..<numColumns).asSequence().map { Coordinate(row, it) } }
            .first { predicate(get(it)) }
    }

    fun rowIndices(): IntRange {
        return 0..<numRows
    }

    fun columnIndices(): IntRange {
        return 0..<numColumns
    }

    fun entries(): Sequence<Pair<Coordinate, T>> {
        return sequence {
            rowIndices().forEach { row ->
                columnIndices().forEach { column ->
                    yield(Pair(Coordinate(row, column), get(row, column)))
                }
            }
        }
    }

    fun rows(): Sequence<Sequence<T>> {
        return rowIndices()
            .asSequence()
            .map { row(it) }
    }

    fun columns(): Sequence<Sequence<T>> {
        return columnIndices()
            .asSequence()
            .map { column(it) }
    }

    fun materialized(): TwoDimensionalArray<T> {
        val snapshot = List(numRows) { i ->
            List(numColumns) { j ->
                get(i, j)
            }.toList()
        }.toList()

        return TwoDimensionalArray(numRows, numColumns) { row, column -> snapshot[row][column]}
    }

    companion object {
        fun charsFromLines(lines: List<String>): TwoDimensionalArray<Char> {
            return TwoDimensionalArray(lines.size, lines[0].length) { i, j -> lines[i][j] }
        }
    }
}

data class Vector2d(val x: Int, val y: Int)

data class Coordinate(val row: Int, val column: Int) {
    operator fun plus(delta: Pair<Int, Int>): Coordinate {
        return Coordinate(row + delta.first, column + delta.second)
    }

    fun manhattanDistance(other: Coordinate): Int {
        return abs(row - other.row) + abs(column - other.column)
    }
}

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