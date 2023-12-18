package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.cycle
import cz.judas.jan.advent.parserFor

object Day18 {
    @Answer("49578")
    fun part1(input: InputData): Long {
        val parser = parserFor<Part1Line>()
        val path = input.lines()
            .map(parser::parse)
            .map { Step(it.direction, it.amount) }

        return calculateArea(path)
    }

    @Answer("52885384955882")
    fun part2(input: InputData): Long {
        val parser = parserFor<Part2Line>()
        val path = input.lines()
            .map(parser::parse)
            .map { Step(it.direction, it.amount) }

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

    fun calculateArea(steps: List<Step>): Long {
        val orientation = steps.cycle()
            .windowed(2) { it[0].direction.movement.rotateRight().dotProduct(it[1].direction.movement) }
            .take(steps.size)
            .sum()
        val positiveDirection = if (orientation > 0) Direction.LEFT else Direction.RIGHT
        val negativeDirection = positiveDirection.inverse()

        var currentRow = 0L
        var sum = 0L
        for (i in steps.indices) {
            val previous = steps[(i - 1 + steps.size) % steps.size]
            val current = steps[i]
            val previousRow = currentRow
            currentRow += current.direction.movement.rows * current.amount
            if (current.direction == positiveDirection) {
                sum += (current.amount - 1) * currentRow
                if (previous.direction == Direction.UP) {
                    sum += currentRow
                }
            } else if(previous.direction == positiveDirection && current.direction == Direction.DOWN) {
                sum += previousRow
            } else if (current.direction == negativeDirection) {
                sum -= (current.amount - 1) * (currentRow + 1)
                if (previous.direction == Direction.DOWN) {
                    sum -= currentRow + 1
                }
            } else if (previous.direction == negativeDirection && current.direction == Direction.UP) {
                sum -= previousRow + 1
            }
            sum += current.amount
        }
        return sum
    }

    data class Step(val direction: Direction, val amount: Int)
}