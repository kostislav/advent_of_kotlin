package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.*

object Day3 {
    fun part1(input: InputData): Int {
        return input.lines()
            .map { it.splitInHalf() }
            .map {
                it.map { half -> half.characters().toSet() }
                    .collect{ firstHalfChars, secondHalfChars -> firstHalfChars.intersect(secondHalfChars).getOnlyElement()}
            }
            .sumOf(::score)
    }

    private fun score(c: Char): Int {
        return when (c) {
            in 'a'..'z' -> c.code - 'a'.code + 1
            in 'A'..'Z' -> c.code - 'A'.code + 27
            else -> throw RuntimeException("Unexpected character ${c}")
        }
    }
}