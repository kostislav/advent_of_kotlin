package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.customParser

object Day9 {
    // one day, this might become just
    // private val parser = parserFor<@SplitOnPattern("\\s+") List<Int>>()
    private val parser = customParser { line -> line.split(" ").map { it.toInt() } }

    @Answer("1884768153")
    fun part1(input: InputData): Int {
        return input.lines()
            .sumOf { line ->
                recurse(parser.parse(line)) { values, diff -> values.last() + diff }
            }
    }

    @Answer("1031")
    fun part2(input: InputData): Int {
        return input.lines()
            .sumOf { line ->
                recurse(parser.parse(line)) { values, diff -> values[0] - diff }
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