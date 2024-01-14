package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.PathSegment
import cz.judas.jan.advent.calculateArea
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
        return calculateArea(steps, includeBorder = false).toInt()
    }

    private fun findPath(input: InputData): List<PathSegment> {
        val pipeTypes = mapOf(
            '-' to setOf(Direction.LEFT, Direction.RIGHT),
            '|' to setOf(Direction.UP, Direction.DOWN),
            'L' to setOf(Direction.UP, Direction.RIGHT),
            'J' to setOf(Direction.UP, Direction.LEFT),
            '7' to setOf(Direction.DOWN, Direction.LEFT),
            'F' to setOf(Direction.DOWN, Direction.RIGHT),
            '.' to emptySet()
        )

        val diagram = input.as2dArray()
        val startingPosition = diagram.first { it == 'S' }

        val startingDirections = Direction.entries
            .filter { direction ->
                val neighborPosition = startingPosition + direction.movement
                val neighborDirections = pipeTypes.getValue(diagram.getOrDefault(neighborPosition, '.'))
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
            val nextPosition = step.position + step.direction
            val nextDirection = fixedDiagram[nextPosition].first { it != step.direction.inverse() }
            Step(nextPosition, nextDirection)
        }
            .takeWhileIndexed { i, step -> i == 0 || step.position != startingPosition }
            .map { PathSegment(it.direction, 1) }
            .toList()
    }

    data class Step(
        val position: Coordinate,
        val direction: Direction
    )
}