package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Digit
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.StringFinder
import cz.judas.jan.advent.associateByMultiple
import cz.judas.jan.advent.findAll
import cz.judas.jan.advent.pickByIndex

object Day1 {
    @Answer("54597")
    fun part1(input: InputData): Int {
        return input.lines()
            .sumOf { line ->
                line.filter { it in '0'..'9' }
                    .pickByIndex(0, -1)
                    .joinToString("")
                    .toInt()
            }
    }

    @Answer("54504")
    fun part2(input: InputData): Int {
        val digits = Digit.entries.associateByMultiple { listOf(it.value.toString(), it.name.lowercase()) }
            .filterKeys { it != "zero" }
            .mapValues { it.value.value }
        val finder = StringFinder.forStrings(digits.keys, overlapping = true)

        return input.lines()
            .sumOf { line ->
                line.findAll(finder)
                    .pickByIndex(0, -1)
                    .map { digits.getValue(it.first) }
                    .joinToString("")
                    .toInt()
            }
    }
}