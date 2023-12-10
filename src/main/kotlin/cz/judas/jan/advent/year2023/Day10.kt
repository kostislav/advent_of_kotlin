package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.customParser
import cz.judas.jan.advent.map

object Day10 {
    @Answer("6842")
    fun part1(input: InputData): Int {
        val pipeTypes = mapOf(
            '-' to setOf(Direction.LEFT, Direction.RIGHT),
            '|' to setOf(Direction.UP, Direction.DOWN),
            'L' to setOf(Direction.UP, Direction.RIGHT),
            'J' to setOf(Direction.UP, Direction.LEFT),
            '7' to setOf(Direction.DOWN, Direction.LEFT),
            'F' to setOf(Direction.DOWN, Direction.RIGHT),
        )
        val movement = mapOf(
            Direction.UP to Pair(-1, 0),
            Direction.DOWN to Pair(1, 0),
            Direction.LEFT to Pair(0, -1),
            Direction.RIGHT to Pair(0, 1),
        )

        val diagram = TwoDimensionalArray.charsFromLines(input.lines())
        val startingPosition = (0..<diagram.numRows).asSequence()
            .flatMap { row -> (0..<diagram.numColumns).asSequence().map { Pair(row, it) } }
            .first { diagram[it.first, it.second] == 'S' }

        val startingDirections = Direction.entries
            .filter {
                val nextPosition = startingPosition + movement.getValue(it)
                val field = diagram.getOrNull(nextPosition.first, nextPosition.second)?:'.'
                it in (pipeTypes[field]?:emptySet()).map { it.inverse() }
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

    fun part2(input: InputData): Int {
        return 0
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
}