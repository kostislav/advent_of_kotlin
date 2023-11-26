package cz.judas.jan.advent.year2022

import com.google.common.collect.Range
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.collect
import cz.judas.jan.advent.map
import cz.judas.jan.advent.splitOnOnly

object Day4 {
    fun part1(input: InputData): Int {
        return input.lines()
            .map(::parseRanges)
            .filter { (first, second) -> first.encloses(second) || second.encloses(first) }
            .size
    }

    fun part2(input: InputData): Int {
        return input.lines()
            .map(::parseRanges)
            .filter { (first, second) -> first.isConnected(second) }
            .size
    }

    private fun parseRanges(line: String): Pair<Range<Int>, Range<Int>> {
        return line.splitOnOnly(",").map { parseRange(it) }
    }

    private fun parseRange(input: String): Range<Int> {
        return input.splitOnOnly("-")
            .map { it.toInt() }
            .collect { lower, upper -> Range.closed(lower, upper) }
    }
}