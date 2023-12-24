package cz.judas.jan.advent.year2023

import com.google.common.math.IntMath
import com.google.common.math.LongMath.pow
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.Vector2d
import cz.judas.jan.advent.breadthFirstSearch
import java.math.RoundingMode

object Day21 {
    @Answer("")
    fun part1(input: InputData): Int {
        return part1(input, 64)
    }

    fun part1(input: InputData, steps: Int): Int {
        val garden = input.as2dArray()
        val startingPosition = garden.first { it == 'S' }
        val stepMod = steps % 2
        val visited = mutableSetOf<Coordinate>()
        val goals = mutableSetOf<Coordinate>()
        visited += startingPosition
        if (stepMod == 0) {
            goals += startingPosition
        }
        breadthFirstSearch(startingPosition to 0) { (position, stepsTaken), backlog ->
            if (stepsTaken % 2 == stepMod) {
                goals += position
            }
            if (stepsTaken < steps) {
                val nextPossible = Direction.entries
                    .map { position + it.movement }
                    .filter { garden.isInside(it) && garden[it] != '#' }

                for (next in nextPossible) {
                    if (visited.add(next)) {
                        backlog += next to (stepsTaken + 1)
                    }
                }
            }
        }
        return goals.size
    }

    @Answer("613391294577878")
    fun part2(input: InputData): Long {
        return shadyPart2(input, 26501365)
    }

    fun shadyPart2(input: InputData, steps: Int): Long {
        val oddPrimarySquare = AreaOfInterest(Vector2d(-131, 0), 65)
        val evenPrimarySquare = AreaOfInterest(Vector2d(0, 0), 65)
        val (totalCount, areas) = honestPart2(input, 196, listOf(oddPrimarySquare, evenPrimarySquare))
        val oddPrimarySquareCount = areas.getValue(oddPrimarySquare)
        val evenPrimarySquareCount = areas.getValue(evenPrimarySquare)
        val secondarySquareCount = (totalCount - evenPrimarySquareCount - 4 * oddPrimarySquareCount) / 2

        val numGardens = (steps - 65L) / 131

        return pow(numGardens + 1, 2) * oddPrimarySquareCount + pow(numGardens, 2) * evenPrimarySquareCount + (pow(2 * numGardens + 1, 2) - 1) / 4 * secondarySquareCount
    }

    fun honestPart2(input: InputData, steps: Int): Long {
        return honestPart2(input, steps, emptyList()).first
    }

    fun honestPart2(input: InputData, steps: Int, additionalAreas: List<AreaOfInterest>): Pair<Long, Map<AreaOfInterest, Long>> {
        val garden = input.as2dArray()
        val extraGardens = IntMath.divide(steps - garden.numRows / 2, garden.numRows, RoundingMode.CEILING)
        val startingPosition = garden.first { it == 'S' }.let { Coordinate(it.row + garden.numRows * extraGardens, it.column + garden.numColumns * extraGardens) }
        val megaGarden = TwoDimensionalArray(garden.numRows * (2 * extraGardens + 1), garden.numColumns * (2 * extraGardens + 1)) { row, column ->
            val original = garden[row % garden.numRows, column % garden.numColumns]
            if (original == 'S') '.' else original
        }
        val visited = mutableSetOf<Coordinate>()
        val stepMod = steps % 2
        val goals = mutableSetOf<Coordinate>()
        if (stepMod == 0) {
            goals += startingPosition
        }
        visited += startingPosition
        breadthFirstSearch(startingPosition to 0) { (position, stepsTaken), backlog ->
            if (stepsTaken % 2 == stepMod) {
                goals += position
            }
            if (stepsTaken < steps) {
                val nextPossible = Direction.entries
                    .map { position + it.movement }
                    .filter { megaGarden.isInside(it) && megaGarden[it] != '#' }

                for (next in nextPossible) {
                    if (visited.add(next)) {
                        backlog += next to (stepsTaken + 1)
                    }
                }
            }
        }

        val additionalAreaResults = additionalAreas.associateWith { (offset, radius) ->
            val center = startingPosition + offset
            goals.count { it.manhattanDistance(center) <= radius }.toLong()
        }

        return Pair(goals.size.toLong(), additionalAreaResults)
    }

    data class AreaOfInterest(val offset: Vector2d, val radius: Int)
}
