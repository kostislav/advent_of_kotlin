package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.RelativeDirection
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.shortestPath

object Day18 {
    fun part1(input: InputData): Int {
        val parser = parserFor<Line>()
        val path = mutableListOf<Day10.Step>()
        var currentPosition = Coordinate(0, 0)
        for (line in input.lines()) {
            val parsedLine = parser.parse(line)
            for (i in 1..parsedLine.amount) {
                path += Day10.Step(currentPosition, parsedLine.direction)
                currentPosition += parsedLine.direction.movement
            }
        }

        return Day10.calculateArea(path) + path.size
    }

    fun part2(input: InputData): Int {
        return 0
    }

    @Pattern("([A-Z]) (\\d+)\\s+\\((.+)\\)")
    class Line(
        directionChar: String,  // TODO Char
        val amount: Int,
        val color: String
    ) {
        val direction = when(directionChar) {
            "U" -> Direction.UP
            "D" -> Direction.DOWN
            "L" -> Direction.LEFT
            "R" -> Direction.RIGHT
            else -> throw RuntimeException("Unexpected direction")
        }
    }
}