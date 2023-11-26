package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.*

object Day3 {
    fun part1(input: InputData): Int {
        return input.lines()
            .map { it.splitInHalf() }
            .map {
                it.map { half -> half.setOfChars() }
                    .collect{ firstHalfChars, secondHalfChars -> firstHalfChars.intersect(secondHalfChars).getOnlyElement()}
            }
            .sumOf(::score)
    }

    fun part2(input: InputData): Int {
        return input.lines()
            .chunked(3)
            .map { group -> group.map { it.setOfChars() }.intersection().getOnlyElement() }
            .sumOf(::score)
    }

    private fun String.setOfChars(): Set<Char> {
        return characters().toSet()
    }

    private fun score(c: Char): Int {
        return when (c) {
            in 'a'..'z' -> c.code - 'a'.code + 1
            in 'A'..'Z' -> c.code - 'A'.code + 27
            else -> throw RuntimeException("Unexpected character ${c}")
        }
    }
}