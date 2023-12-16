package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.cycle
import cz.judas.jan.advent.takeWhileIndexed

object Day10 {
    @Answer("6842")
    fun part1(input: InputData): Int {
        val steps = findPath(input)
        return steps.count() / 2
    }

    @Answer("393")
    fun part2(input: InputData): Int {
        val steps = findPath(input)
        val orientation = steps.cycle()
            .windowed(2) { it[0].direction.movement.rotateRight().dotProduct(it[1].direction.movement) }
            .take(steps.size)
            .sum()
        val positiveDirection = if (orientation > 0) Direction.LEFT else Direction.RIGHT
        val negativeDirection = positiveDirection.inverse()
        val positiveCombinations = setOf(
            Pair(positiveDirection, positiveDirection),
            Pair(Direction.UP, positiveDirection),
            Pair(positiveDirection, Direction.DOWN)
        )
        val negativeCombinations = setOf(
            Pair(negativeDirection, negativeDirection),
            Pair(Direction.DOWN, negativeDirection),
            Pair(negativeDirection, Direction.UP)
        )

        return steps.cycle()
            .windowed(2) {
                val directions = Pair(it[0].direction, it[1].direction)
                when (directions) {
                    in positiveCombinations -> it[1].position.row
                    in negativeCombinations -> -it[1].position.row - 1
                    else -> 0
                }
            }
            .take(steps.size)
            .sum()
    }

    private fun findPath(input: InputData): List<Step> {
        val pipeTypes = mapOf(
            '-' to setOf(Direction.LEFT, Direction.RIGHT),
            '|' to setOf(Direction.UP, Direction.DOWN),
            'L' to setOf(Direction.UP, Direction.RIGHT),
            'J' to setOf(Direction.UP, Direction.LEFT),
            '7' to setOf(Direction.DOWN, Direction.LEFT),
            'F' to setOf(Direction.DOWN, Direction.RIGHT),
            '.' to emptySet()
        )

        val diagram = TwoDimensionalArray.charsFromLines(input.lines())
        val startingPosition = diagram.first { it == 'S' }

        val startingDirections = Direction.entries
            .filter { direction ->
                val neighborPosition = startingPosition + direction.movement
                val neighborDirections = pipeTypes.getValue(diagram.getOrNull(neighborPosition) ?: '.')
                direction.inverse() in neighborDirections
            }
            .toSet()

        val fixedDiagram = diagram.map {
            if (it == 'S') {
                startingDirections
            } else {
                pipeTypes[it] ?: emptySet()
            }
        }

        return generateSequence(Step(startingPosition, fixedDiagram[startingPosition].first())) { step ->
            val nextPosition = step.nextPosition()
            val nextDirection = fixedDiagram[nextPosition].first { it != step.direction.inverse() }
            Step(nextPosition, nextDirection)
        }
            .takeWhileIndexed { i, step -> i == 0 || step.position != startingPosition }
            .toList()
    }

    data class Step(
        val position: Coordinate,
        val direction: Direction
    ) {
        fun nextPosition(): Coordinate {
            return position + direction.movement
        }
    }

}