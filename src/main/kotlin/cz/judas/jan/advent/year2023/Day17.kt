package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.RelativeDirection
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.shortestPath

object Day17 {
    @Answer("902")
    fun part1(input: InputData): Int {
        return calculate(input, 0, 3)
    }

    @Answer("1073")
    fun part2(input: InputData): Int {
        return calculate(input, 4, 10)
    }

    private fun calculate(input: InputData, minStraight: Int, maxStraight: Int): Int {
        val city = TwoDimensionalArray.charsFromLines(input.lines()).map { it.digitToInt() }.materialized()
        val virtualStartingNode = Hypercoordinate(Coordinate(-1, -1), Direction.LEFT, 0)
        val targetPosition = Coordinate(city.numRows - 1, city.numColumns - 1)
        val virtualTargetNode = Hypercoordinate(Coordinate(city.numRows, city.numColumns), Direction.RIGHT, 0)
        return shortestPath(virtualStartingNode, virtualTargetNode) { current ->
            if (current == virtualStartingNode) {
                mapOf(
                    Hypercoordinate(Coordinate(0, 0), Direction.RIGHT, 1) to 0,
                    Hypercoordinate(Coordinate(0, 0), Direction.DOWN, 1) to 0
                )
            } else if (current.position == targetPosition) {
                if (current.steps >= minStraight) {
                    mapOf(virtualTargetNode to 0)
                } else {
                    emptyMap()
                }
            } else {
                buildMap {
                    if (current.steps >= minStraight) {
                        put(RelativeDirection.LEFT, 1)
                        put(RelativeDirection.RIGHT, 1)
                    }
                    if (current.steps < maxStraight) {
                        put(RelativeDirection.FORWARD, current.steps + 1)
                    }
                }
                    .map { (relativeDirection, nextSteps) ->
                        val nextDirection = current.direction.move(relativeDirection)
                        Hypercoordinate(current.position + nextDirection.movement, nextDirection, nextSteps)
                    }
                    .filter { city.isInside(it.position) }
                    .associateWith { city[it.position] }
                    .toMap()
            }
        }
    }

    data class Hypercoordinate(
        val position: Coordinate,
        val direction: Direction,
        val steps: Int
    )
}