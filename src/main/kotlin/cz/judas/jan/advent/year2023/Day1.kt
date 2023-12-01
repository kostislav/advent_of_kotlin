package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Digit
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.findAll
import cz.judas.jan.advent.pickByIndex

object Day1 {
    fun part1(input: InputData): Int {
        return input.lines()
            .sumOf { line ->
                line.filter { it in '0'..'9' }
                    .pickByIndex(0, -1)
                    .joinToString("")
                    .toInt()
            }
    }

    fun part2(input: InputData): Int {
        val digits = Digit.entries.associate { it.value.toString() to it.value } +
                Digit.entries.filter { it != Digit.ZERO }.associate { it.name.lowercase() to it.value }

        return input.lines()
            .sumOf { line ->
                line.findAll(digits.keys, overlapping = true)
                    .pickByIndex(0, -1)
                    .map { digits.getValue(it.first) }
                    .joinToString("")
                    .toInt()
            }
    }
}