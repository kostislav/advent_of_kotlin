package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray

object Day17 {
    fun part1(input: InputData): Int {
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
            for (next in current.next(city.numRows, city.numColumns)) {
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

    fun part2(input: InputData): Int {
        val city = TwoDimensionalArray.charsFromLines(input.lines()).map { it.digitToInt() }.materialized()
        val best = mutableMapOf<Hypercoordinate2, Int>()
        val backlog = ArrayDeque<Hypercoordinate2>()
        backlog += Hypercoordinate2(Coordinate(0, 0), Direction.RIGHT, 1)
        backlog += Hypercoordinate2(Coordinate(0, 0), Direction.DOWN, 1)
        best[Hypercoordinate2(Coordinate(0, 0), Direction.RIGHT, 1)] = 0
        best[Hypercoordinate2(Coordinate(0, 0), Direction.DOWN, 1)] = 0

        while (backlog.isNotEmpty()) {
            val current = backlog.removeFirst()
            val currentLoss = best.getValue(current)
            for (next in current.next(city.numRows, city.numColumns)) {
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
        fun next(numRows: Int, numColumns: Int): List<Hypercoordinate> {
            val result = mutableListOf<Hypercoordinate?>()
            if (position.row > 0) {
                result += nextIfAllowed(Direction.UP)
            }
            if (position.row < numRows - 1) {
                result += nextIfAllowed(Direction.DOWN)
            }
            if (position.column > 0) {
                result += nextIfAllowed(Direction.LEFT)
            }
            if (position.column < numColumns - 1) {
                result += nextIfAllowed(Direction.RIGHT)
            }

            return result.filterNotNull()
        }

        private fun nextIfAllowed(nextDirection: Direction): Hypercoordinate? {
            return if (nextDirection == direction) {
                if (steps < 3) {
                    Hypercoordinate(position + nextDirection.movement, nextDirection, steps + 1)
                } else {
                    null
                }
            } else if (nextDirection != direction.inverse()) {
                Hypercoordinate(position + nextDirection.movement, nextDirection, 1)
            } else {
                null
            }
        }
    }

    data class Hypercoordinate2(
        val position: Coordinate,
        val direction: Direction,
        val steps: Int
    ) {
        fun next(numRows: Int, numColumns: Int): List<Hypercoordinate2> {
            val result = mutableListOf<Hypercoordinate2?>()
            if (position.row > 0) {
                result += nextIfAllowed(Direction.UP)
            }
            if (position.row < numRows - 1) {
                result += nextIfAllowed(Direction.DOWN)
            }
            if (position.column > 0) {
                result += nextIfAllowed(Direction.LEFT)
            }
            if (position.column < numColumns - 1) {
                result += nextIfAllowed(Direction.RIGHT)
            }

            return result.filterNotNull()
        }

        private fun nextIfAllowed(nextDirection: Direction): Hypercoordinate2? {
            return if (nextDirection == direction) {
                if (steps < 10) {
                    Hypercoordinate2(position + nextDirection.movement, nextDirection, steps + 1)
                } else {
                    null
                }
            } else if (nextDirection != direction.inverse() && steps >= 4) {
                Hypercoordinate2(position + nextDirection.movement, nextDirection, 1)
            } else {
                null
            }
        }
    }
}