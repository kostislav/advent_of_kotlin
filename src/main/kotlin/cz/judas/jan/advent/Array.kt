package cz.judas.jan.advent

import kotlin.math.abs

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

    fun rotateLeft(): TwoDimensionalArray<T> {
        return TwoDimensionalArray(numColumns, numRows) { row, column -> get(column, numRows - 1 - row) }
    }

    fun transpose(): TwoDimensionalArray<T> {
        return TwoDimensionalArray(numColumns, numRows) { row, column -> get(column, row) }
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
        return TwoDimensionalArray(numRows, numColumns) { row, column -> transformation(get(row, column)) }
    }

    fun <O> mapIndexed(transformation: (row: Int, column: Int, T) -> O): TwoDimensionalArray<O> {
        return TwoDimensionalArray(numRows, numColumns) { row, column -> transformation(row, column, get(row, column)) }
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

        return TwoDimensionalArray(numRows, numColumns) { row, column -> snapshot[row][column] }
    }

    fun isInside(row: Int, column: Int): Boolean {
        return row in 0..<numRows && column in 0..<numColumns
    }

    fun isInside(position: Coordinate): Boolean {
        return isInside(position.row, position.column)
    }

    companion object {
        fun charsFromLines(lines: List<String>): TwoDimensionalArray<Char> {
            return TwoDimensionalArray(lines.size, lines[0].length) { i, j -> lines[i][j] }
        }
    }
}

data class Coordinate(val row: Int, val column: Int) {
    operator fun plus(delta: Vector2d): Coordinate {
        return Coordinate(row + delta.rows, column + delta.columns)
    }

    fun manhattanDistance(other: Coordinate): Int {
        return abs(row - other.row) + abs(column - other.column)
    }
}

class Mutable2dArray<T> private constructor(
    private val items: MutableList<MutableList<T>>,
    private val defaultValue: T
) {
    val numRows get() = items.size

    val numColumns get() = items[0].size

    fun set(row: Int, column: Int, value: T) {
        items[row][column] = value
    }

    fun setSafe(row: Int, column: Int, value: T) {
        if (isInside(row, column)) {
            items[row][column] = value
        }
    }

    fun getSafe(row: Int, column: Int): T {
        return if (isInside(row, column)) {
            items[row][column]
        } else {
            defaultValue
        }
    }

    fun isInside(row: Int, column: Int): Boolean {
        return row in 0..<numRows && column in 0..<numColumns
    }

    fun forEach(action: (Int, Int, T) -> Unit) {
        for (row in 0..<numRows) {
            for (column in 0..<numColumns) {
                action(row, column, getSafe(row, column))
            }
        }
    }

    fun count(predicate: (T) -> Boolean): Int {
        return (0..<numRows).sumOf { row ->
            (0..<numColumns).map { column ->
                if (predicate(getSafe(row, column))) 1 else 0
            }.sum()
        }
    }

    fun floodFill(startingPosition: Pair<Int, Int>, value: T) {
        val (row, column) = startingPosition
        if (isInside(row - 1, column - 1) && getSafe(row - 1, column - 1) === null) {
            set(row - 1, column - 1, value)
        }
        if (isInside(row - 1, column) && getSafe(row - 1, column) === null) {
            set(row - 1, column, value)
        }
        if (isInside(row - 1, column + 1) && getSafe(row - 1, column + 1) === null) {
            set(row - 1, column + 1, value)
        }
        if (isInside(row, column - 1) && getSafe(row, column - 1) === null) {
            set(row, column - 1, value)
        }
        if (isInside(row, column + 1) && getSafe(row, column + 1) === null) {
            set(row, column + 1, value)
        }
        if (isInside(row + 1, column - 1) && getSafe(row + 1, column - 1) === null) {
            set(row + 1, column - 1, value)
        }
        if (isInside(row + 1, column) && getSafe(row + 1, column) === null) {
            set(row + 1, column, value)
        }
        if (isInside(row + 1, column + 1) && getSafe(row + 1, column + 1) === null) {
            set(row + 1, column + 1, value)
        }
    }

    companion object {
        operator fun <T> invoke(numRows: Int, numColumns: Int, defaultValue: T): Mutable2dArray<T> {
            return Mutable2dArray(
                MutableList(numRows) { i ->
                    MutableList(numColumns) { j ->
                        defaultValue
                    }
                },
                defaultValue
            )
        }
    }
}