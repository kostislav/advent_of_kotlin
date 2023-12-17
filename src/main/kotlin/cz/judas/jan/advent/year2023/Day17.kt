package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.RelativeDirection
import cz.judas.jan.advent.TwoDimensionalArray

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
                mapOf(virtualTargetNode to 0)
            } else {
                current.next(minStraight, maxStraight)
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
    ) {
        fun next(minStraight: Int, maxStraight: Int): List<Hypercoordinate> {
            val result = mutableMapOf<RelativeDirection, Int>()
            if (steps >= minStraight) {
                result[RelativeDirection.LEFT] = 1
                result[RelativeDirection.RIGHT] = 1
            }
            if (steps < maxStraight) {
                result[RelativeDirection.FORWARD] = steps + 1
            }
            return result.map { (relativeDirection, nextSteps) ->
                val nextDirection = direction.move(relativeDirection)
                Hypercoordinate(position + nextDirection.movement, nextDirection, nextSteps)
            }
        }
    }

    fun <N> shortestPath(startingNode: N, targetNode: N, edgeSupplier: (N) -> Map<N, Int>): Int {
        val best = mutableMapOf<N, Int>()
        val backlog = ArrayDeque<N>()
        backlog += startingNode
        best[startingNode] = 0

        while (backlog.isNotEmpty()) {
            val current = backlog.removeFirst()
            val currentLength = best.getValue(current)
            for ((next, weight) in edgeSupplier(current)) {
                val heatLoss = currentLength + weight
                val nextLength = best[next]
                if (nextLength === null || nextLength > heatLoss) {
                    best[next] = heatLoss
                    backlog += next
                }
            }
        }
        return best.getValue(targetNode)
    }
}