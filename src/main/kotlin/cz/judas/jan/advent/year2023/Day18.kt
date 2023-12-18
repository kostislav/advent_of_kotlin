package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.PathSegment
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.calculateArea
import cz.judas.jan.advent.parserUsing

object Day18 {
    @Answer("49578")
    fun part1(input: InputData): Long {
        val parser = parserUsing(Part1Parser::parseLine)
        val path = input.lines()
            .map(parser::parse)
            .map { PathSegment(it.direction, it.amount) }

        return calculateArea(path, includeBorder = true)
    }

    @Answer("52885384955882")
    fun part2(input: InputData): Long {
        val parser = parserUsing(Part2Parser::parseLine)
        val path = input.lines()
            .map(parser::parse)
            .map { PathSegment(it.direction, it.amount) }

        return calculateArea(path, includeBorder = true)
    }

    object Part1Parser {
        private val directionMapping = mapOf(
            "U" to Direction.UP,
            "D" to Direction.DOWN,
            "L" to Direction.LEFT,
            "R" to Direction.RIGHT
        )

        @Pattern("([A-Z]) (\\d+)\\s+.+")
        fun parseLine(direction: String /* TODO Char */, amount: Int): PathSegment {
            return PathSegment(
                directionMapping.getValue(direction),
                amount
            )
        }
    }

    object Part2Parser {
        private val directionMapping = mapOf(
            "0" to Direction.RIGHT,
            "1" to Direction.DOWN,
            "2" to Direction.LEFT,
            "3" to Direction.UP
        )

        @Pattern(".+\\(#(.+)(.)\\)")
        fun parseLine(amount: String /* TODO Radix */, direction: String): PathSegment {
            return PathSegment(
                directionMapping.getValue(direction),
                amount.toInt(16)
            )
        }
    }
}