package cz.judas.jan.advent.year2015

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.collect
import cz.judas.jan.advent.map
import cz.judas.jan.advent.partitionIndexed
import cz.judas.jan.advent.translate

object Day3 {
    private val directions = mapOf(
        '^' to Direction.UP,
        '>' to Direction.RIGHT,
        'v' to Direction.DOWN,
        '<' to Direction.LEFT,
    )

    @Answer("2565")
    fun part1(input: InputData): Int {
        return traverse(input.asString().asIterable()).size
    }

    @Answer("2639")
    fun part2(input: InputData): Int {
        return input.asString().asIterable()
            .partitionIndexed { index, _ -> index % 2 == 0 }
            .map(::traverse)
            .collect { first, second -> first + second}
            .size
    }

    private fun traverse(route: Iterable<Char>): Set<Coordinate> {
        return route
            .translate(directions)
            .scan(Coordinate(0, 0)) { current, direction -> current + direction }
            .toSet()
    }
}
