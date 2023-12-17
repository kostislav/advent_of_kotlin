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
        val best = mutableMapOf<Hypercoordinate, Int>()
        val backlog = ArrayDeque<Hypercoordinate>()
        backlog += Hypercoordinate(Coordinate(0, 0), Direction.RIGHT, 1)
        backlog += Hypercoordinate(Coordinate(0, 0), Direction.DOWN, 1)
        best[Hypercoordinate(Coordinate(0, 0), Direction.RIGHT, 1)] = 0
        best[Hypercoordinate(Coordinate(0, 0), Direction.DOWN, 1)] = 0

        while (backlog.isNotEmpty()) {
            val current = backlog.removeFirst()
            val currentLoss = best.getValue(current)
            for (next in current.next(minStraight, maxStraight)) {
                if(city.isInside(next.position)) {
                    val heatLoss = currentLoss + city[next.position]
                    val nextLoss = best[next]
                    if (nextLoss === null || nextLoss > heatLoss) {
                        best[next] = heatLoss
                        backlog += next
                    }
                }
            }
        }
        return best.filterKeys { it.position == Coordinate(city.numRows - 1, city.numColumns - 1) }
            .values
            .min()
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
            return result.map {
                (relativeDirection, nextSteps) ->
                val nextDirection = direction.move(relativeDirection)
                Hypercoordinate(position + nextDirection.movement, nextDirection, nextSteps)
            }
        }
    }
}