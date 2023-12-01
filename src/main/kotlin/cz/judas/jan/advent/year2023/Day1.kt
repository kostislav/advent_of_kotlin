package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Digit
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.characters
import cz.judas.jan.advent.firstValue
import cz.judas.jan.advent.lastValue
import cz.judas.jan.advent.toSortedMap

object Day1 {
    fun part1(input: InputData): Int {
        return input.lines()
            .map { line ->
                line.characters()
                    .filter { it in '0'..'9' }
                    .let { "${it[0]}${it.last()}" }
            }
            .sumOf { it.toInt() }
    }

    fun part2(input: InputData): Int {
        val digits = ('0'..'9').associateBy { it.toString() } +
                Digit.entries.filter { it != Digit.ZERO }.associate { it.name.lowercase() to it.value }

        return input.lines()
            .map { line ->
                digits
                    .flatMap { (key, value) ->
                        listOf(line.indexOf(key), line.lastIndexOf(key))
                            .filter { it != -1 }
                            .map { it to value }
                    }
                    .toSortedMap()
                    .let { "${it.firstValue()}${it.lastValue()}" }

            }
            .sumOf { it.toInt() }
    }
}