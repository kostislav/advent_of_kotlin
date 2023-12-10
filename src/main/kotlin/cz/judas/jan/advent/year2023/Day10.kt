package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray

object Day10 {
    private val pipeTypes = mapOf(
        '-' to setOf(Direction.LEFT, Direction.RIGHT),
        '|' to setOf(Direction.UP, Direction.DOWN),
        'L' to setOf(Direction.UP, Direction.RIGHT),
        'J' to setOf(Direction.UP, Direction.LEFT),
        '7' to setOf(Direction.DOWN, Direction.LEFT),
        'F' to setOf(Direction.DOWN, Direction.RIGHT),
    )
    private val movement = mapOf(
        Direction.UP to Pair(-1, 0),
        Direction.DOWN to Pair(1, 0),
        Direction.LEFT to Pair(0, -1),
        Direction.RIGHT to Pair(0, 1),
    )

    @Answer("6842")
    fun part1(input: InputData): Int {
        val diagram = TwoDimensionalArray.charsFromLines(input.lines())
        val startingPosition = (0..<diagram.numRows).asSequence()
            .flatMap { row -> (0..<diagram.numColumns).asSequence().map { Pair(row, it) } }
            .first { diagram[it.first, it.second] == 'S' }

        val startingDirections = Direction.entries
            .filter {
                val nextPosition = startingPosition + movement.getValue(it)
                val field = diagram.getOrNull(nextPosition.first, nextPosition.second) ?: '.'
                it in (pipeTypes[field] ?: emptySet()).map { it.inverse() }
            }

        var length = 1
        var currentDirection = startingDirections[0]
        var currentPosition = startingPosition + movement.getValue(currentDirection)
        while (currentPosition != startingPosition) {
            currentDirection = pipeTypes.getValue(diagram[currentPosition])
                .first { it != currentDirection.inverse() }
            currentPosition += movement.getValue(currentDirection)
            length += 1
        }
        return length / 2
    }

    @Answer("393")
    fun part2(input: InputData): Int {
        val diagram = TwoDimensionalArray.charsFromLines(input.lines())
        val startingPosition = (0..<diagram.numRows).asSequence()
            .flatMap { row -> (0..<diagram.numColumns).asSequence().map { Pair(row, it) } }
            .first { diagram[it.first, it.second] == 'S' }

        val startingDirections = Direction.entries
            .filter {
                val nextPosition = startingPosition + movement.getValue(it)
                val field = diagram.getOrNull(nextPosition.first, nextPosition.second) ?: '.'
                it in (pipeTypes[field] ?: emptySet()).map { it.inverse() }
            }

        val analyzed = Mutable2dArray<FieldType?>(diagram.numRows, diagram.numColumns, null)
        var currentDirection = startingDirections[0]
        var currentPosition = startingPosition + movement.getValue(currentDirection)
        var rightBias = 0
        while (currentPosition != startingPosition) {
            val previousDirection = currentDirection
            currentDirection = pipeTypes.getValue(diagram[currentPosition])
                .first { it != currentDirection.inverse() }

            when (previousDirection) {
                Direction.UP ->
                    when (currentDirection) {
                        Direction.UP -> {
                            updateField(analyzed, currentPosition, 0, -1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 0, 1, FieldType.RIGH)
                        }

                        Direction.RIGHT -> {
                            updateField(analyzed, currentPosition, 0, -1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, -1, -1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, -1, 0, FieldType.LEFT)
                            updateField(analyzed, currentPosition, +1, 1, FieldType.RIGH)
                            rightBias += 1
                        }

                        Direction.LEFT -> {
                            updateField(analyzed, currentPosition, 0, 1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, 1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, -1, FieldType.LEFT)
                            rightBias -= 1
                        }

                        else -> {}
                    }

                Direction.DOWN -> {
                    when (currentDirection) {
                        Direction.DOWN -> {
                            updateField(analyzed, currentPosition, 0, 1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 0, -1, FieldType.RIGH)
                        }

                        Direction.RIGHT -> {
                            updateField(analyzed, currentPosition, 0, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, 1, FieldType.LEFT)
                            rightBias -= 1
                        }

                        Direction.LEFT -> {
                            updateField(analyzed, currentPosition, -1, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 0, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 1, 1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 0, 1, FieldType.LEFT)
                            rightBias += 1
                        }

                        else -> {}
                    }
                }

                Direction.LEFT -> {
                    when (currentDirection) {
                        Direction.LEFT -> {
                            updateField(analyzed, currentPosition, -1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 0, FieldType.LEFT)
                        }
                        Direction.UP -> {
                            updateField(analyzed, currentPosition, -1, 1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 0, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 1, -1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 0, -1, FieldType.LEFT)
                            rightBias += 1
                        }
                        Direction.DOWN -> {
                            updateField(analyzed, currentPosition, -1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 0, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 1, FieldType.LEFT)
                            rightBias -= 1
                        }
                        else -> {}
                    }
                }

                Direction.RIGHT -> {
                    when (currentDirection) {
                        Direction.RIGHT -> {
                            updateField(analyzed, currentPosition, 1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, 0, FieldType.LEFT)
                        }
                        Direction.UP -> {
                            updateField(analyzed, currentPosition, 1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 0, 1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, -1, FieldType.LEFT)
                            rightBias -= 1
                        }
                        Direction.DOWN -> {
                            updateField(analyzed, currentPosition, 1, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, 0, FieldType.LEFT)
                            updateField(analyzed, currentPosition, -1, 1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 0, 1, FieldType.LEFT)
                            rightBias += 1
                        }
                        else -> {}
                    }
                }
            }

            currentPosition += movement.getValue(currentDirection)
            analyzed.set(currentPosition.first, currentPosition.second, FieldType.LOOP)
        }
        val lookingFor = if (rightBias > 0) FieldType.RIGH else FieldType.LEFT
        while (true) {
            var foundMore = false
            var foundCount = 0
            for (row in 0..<analyzed.numRows) {
                for (column in 0..<analyzed.numColumns) {
                    if (analyzed.getSafe(row, column) == lookingFor) {
                        foundCount += 1
                        if (analyzed.isInside(row - 1, column - 1) && analyzed.getSafe(row - 1, column - 1) === null) {
                            foundMore = true
                            analyzed.set(row - 1, column - 1, lookingFor)
                        }
                        if (analyzed.isInside(row - 1, column) && analyzed.getSafe(row - 1, column) === null) {
                            foundMore = true
                            analyzed.set(row - 1, column, lookingFor)
                        }
                        if (analyzed.isInside(row - 1, column + 1) && analyzed.getSafe(row - 1, column + 1) === null) {
                            foundMore = true
                            analyzed.set(row - 1, column + 1, lookingFor)
                        }
                        if (analyzed.isInside(row, column - 1) && analyzed.getSafe(row, column - 1) === null) {
                            foundMore = true
                            analyzed.set(row, column - 1, lookingFor)
                        }
                        if (analyzed.isInside(row, column + 1) && analyzed.getSafe(row, column + 1) === null) {
                            foundMore = true
                            analyzed.set(row, column + 1, lookingFor)
                        }
                        if (analyzed.isInside(row + 1, column - 1) && analyzed.getSafe(row + 1, column - 1) === null) {
                            foundMore = true
                            analyzed.set(row + 1, column - 1, lookingFor)
                        }
                        if (analyzed.isInside(row + 1, column) && analyzed.getSafe(row + 1, column) === null) {
                            foundMore = true
                            analyzed.set(row + 1, column, lookingFor)
                        }
                        if (analyzed.isInside(row + 1, column + 1) && analyzed.getSafe(row + 1, column + 1) === null) {
                            foundMore = true
                            analyzed.set(row + 1, column + 1, lookingFor)
                        }
                    }
                }
            }
            if (!foundMore) {
                return foundCount
            }
        }
//        analyzed.items.forEach { println(it) } // TODO

//        return 0
    }

    fun updateField(
        field: Mutable2dArray<FieldType?>,
        currentPosition: Pair<Int, Int>,
        rowOffset: Int,
        columnOffset: Int,
        value: FieldType
    ) {
        val targetRow = currentPosition.first + rowOffset
        val targetColumn = currentPosition.second + columnOffset
        if (field.getSafe(targetRow, targetColumn) != FieldType.LOOP) {
            field.setSafe(targetRow, targetColumn, value)
        }
    }

    operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(first + other.first, second + other.second)
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT;

        fun inverse(): Direction {
            return when (this) {
                UP -> DOWN
                DOWN -> UP
                LEFT -> RIGHT
                RIGHT -> LEFT
            }
        }
    }

    enum class FieldType {
        LOOP, LEFT, RIGH  /* TODO */
    }

    class Mutable2dArray<T> private constructor(/*private TODO*/ val items: MutableList<MutableList<T>>,
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
}