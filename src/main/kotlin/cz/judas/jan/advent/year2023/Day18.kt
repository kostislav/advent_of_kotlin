package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.PathSegment
import cz.judas.jan.advent.calculateArea
import cz.judas.jan.advent.mapParsing

object Day18 {
    @Answer("49578")
    fun part1(input: InputData): Long {
        val directionMapping = mapOf(
            "U" to Direction.UP,
            "D" to Direction.DOWN,
            "L" to Direction.LEFT,
            "R" to Direction.RIGHT
        )

        val path = input.lines()
            .mapParsing("([A-Z]) (\\d+)\\s+.+") { direction: String, amount: Int ->
                PathSegment(
                    directionMapping.getValue(direction),
                    amount
                )
            }

        return calculateArea(path, includeBorder = true)
    }

    @Answer("52885384955882")
    fun part2(input: InputData): Long {
        val directionMapping = mapOf(
            "0" to Direction.RIGHT,
            "1" to Direction.DOWN,
            "2" to Direction.LEFT,
            "3" to Direction.UP
        )

        val path = input.lines()
            .mapParsing(".+\\(#(.+)(.)\\)") { amount: String, direction: String ->
                PathSegment(
                    directionMapping.getValue(direction),
                    amount.toInt(16)
                )
            }

        return calculateArea(path, includeBorder = true)
    }
}