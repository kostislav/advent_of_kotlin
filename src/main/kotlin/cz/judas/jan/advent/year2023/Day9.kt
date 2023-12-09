package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData

object Day9 {
    private val whitespace = Regex("\\s+")

    @Answer("1884768153")
    fun part1(input: InputData): Int {
        return solve(input) { values, diff -> values.last() + diff }
    }

    @Answer("1031")
    fun part2(input: InputData): Int {
        return solve(input) { values, diff -> values[0] - diff }
    }

    private fun solve(input: InputData, operation: (List<Int>, Int) -> Int): Int {
        return input.lines()
            .sumOf { line ->
                line.split(whitespace)
                    .map { it.toInt() }
                    .let { recurse(it, operation) }
            }
    }

    private fun recurse(values: List<Int>, operation: (List<Int>, Int) -> Int): Int {
        return if (values.all { it == 0 }) {
            0
        } else {
            val diffs = values.windowed(2).map { it[1] - it[0] }
            operation(values, recurse(diffs, operation))
        }
    }
}