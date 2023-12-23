package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.breadthFirstSearch

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
        goals += startingPosition
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

    @Answer("")
    fun part2(input: InputData): Long {
        return part2(input, 26501365)
    }

    fun part2(input: InputData, steps: Int): Long {
//        TODO finish this
        return 0
    }
}
