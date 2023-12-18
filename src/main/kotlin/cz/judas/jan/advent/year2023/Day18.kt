package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.PathSegment
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.calculateArea
import cz.judas.jan.advent.parserFor

object Day18 {
    @Answer("49578")
    fun part1(input: InputData): Long {
        val parser = parserFor<Part1Line>()
        val path = input.lines()
            .map(parser::parse)
            .map { PathSegment(it.direction, it.amount) }

        return calculateArea(path)
    }

    @Answer("52885384955882")
    fun part2(input: InputData): Long {
        val parser = parserFor<Part2Line>()
        val path = input.lines()
            .map(parser::parse)
            .map { PathSegment(it.direction, it.amount) }

        return calculateArea(path)
    }

    @Pattern("([A-Z]) (\\d+)\\s+.+")
    class Part1Line(
        directionChar: String,  // TODO Char
        val amount: Int,
    ) {
        val direction = when(directionChar) {
            "U" -> Direction.UP
            "D" -> Direction.DOWN
            "L" -> Direction.LEFT
            "R" -> Direction.RIGHT
            else -> throw RuntimeException("Unexpected direction")
        }
    }

    @Pattern(".+\\(#(.+)(.)\\)")
    class Part2Line(
        amountString: String,
        directionChar: String,
    ) {
        val direction = when(directionChar) {
            "0" -> Direction.RIGHT
            "1" -> Direction.DOWN
            "2" -> Direction.LEFT
            "3" -> Direction.UP
            else -> throw RuntimeException("Unexpected direction")
        }

        val amount = amountString.toInt(16)
    }
}