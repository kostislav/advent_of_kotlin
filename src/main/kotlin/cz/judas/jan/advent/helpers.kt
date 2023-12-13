package cz.judas.jan.advent

import com.google.common.collect.BoundType
import com.google.common.collect.Range
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


class TwoDimensionalArray<out T> private constructor(private val items: List<List<T>>) {
    val numRows get() = items.size

    val numColumns get() = items[0].size

    operator fun get(x: Int, y: Int): T = items[x][y]

    operator fun get(position: Coordinate): T = items[position.row][position.column]

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
        return create(numColumns, numRows) { i, j -> get(numRows - 1 - j, i) }
    }

    fun row(index: Int): List<T> {
        return items[index]
    }

    fun column(index: Int): Sequence<T> {
        return rowIndices()
            .asSequence()
            .map { rowIndex -> get(rowIndex, index) }
    }

    fun <O> map(transformation: (T) -> O): TwoDimensionalArray<O> {
        return TwoDimensionalArray(items.map { it.map(transformation) })
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
        return items
            .asSequence()
            .map { row ->
                columnIndices().asSequence().map { column -> row[column] }
            }
    }

    fun columns(): Sequence<Sequence<T>> {
        return columnIndices()
            .asSequence()
            .map { column ->
                rowIndices().asSequence().map { row -> get(row, column) }
            }
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