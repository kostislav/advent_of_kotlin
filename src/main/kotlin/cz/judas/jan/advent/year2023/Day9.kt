package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData

object Day9 {
    private val whitespace = Regex("\\s+")

    fun part1(input: InputData): Int {
        return input.lines()
            .sumOf { line ->
                line.split(whitespace)
                    .map { it.toInt() }
                    .let(::recurse)
            }
    }

    fun part2(input: InputData): Int {
        return input.lines()
            .sumOf { line ->
                line.split(whitespace)
                    .map { it.toInt() }
                    .let(::recurse2)
            }
    }

    fun recurse(values: List<Int>): Int {
        val diffs = values.windowed(2).map { it[1] - it[0] }
        return if (diffs.all { it == 0 }) {
            0
        } else {
            values.last() + recurse(diffs)
        }
    }

    fun recurse2(values: List<Int>): Int {
        return if (values.all { it == 0 }) {
            0
        } else {
            val diffs = values.windowed(2).map { it[1] - it[0] }
            values[0] - recurse2(diffs)
        }
    }
}