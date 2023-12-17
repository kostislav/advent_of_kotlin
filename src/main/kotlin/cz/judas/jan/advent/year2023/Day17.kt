package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
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
            for (next in current.next(city.numRows, city.numColumns, minStraight, maxStraight)) {
                val heatLoss = currentLoss + city[next.position]
                val nextLoss = best[next]
                if (nextLoss === null || nextLoss > heatLoss) {
                    best[next] = heatLoss
                    backlog += next
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
        fun next(numRows: Int, numColumns: Int, minStraight: Int, maxStraight: Int): List<Hypercoordinate> {
            val result = mutableListOf<Hypercoordinate?>()
            if (position.row > 0) {
                result += nextIfAllowed(Direction.UP, minStraight, maxStraight)
            }
            if (position.row < numRows - 1) {
                result += nextIfAllowed(Direction.DOWN, minStraight, maxStraight)
            }
            if (position.column > 0) {
                result += nextIfAllowed(Direction.LEFT, minStraight, maxStraight)
            }
            if (position.column < numColumns - 1) {
                result += nextIfAllowed(Direction.RIGHT, minStraight, maxStraight)
            }

            return result.filterNotNull()
        }

        private fun nextIfAllowed(nextDirection: Direction, minStraight: Int, maxStraight: Int): Hypercoordinate? {
            return if (nextDirection == direction) {
                if (steps < maxStraight) {
                    Hypercoordinate(position + nextDirection.movement, nextDirection, steps + 1)
                } else {
                    null
                }
            } else if (nextDirection != direction.inverse() && steps >= minStraight) {
                Hypercoordinate(position + nextDirection.movement, nextDirection, 1)
            } else {
                null
            }
        }
    }
}