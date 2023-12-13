package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.recursive
import cz.judas.jan.advent.subList
import cz.judas.jan.advent.times

object Day12 {
    private val rowParser = parserFor<Row>()

    @Answer("7017")
    fun part1(input: InputData): Long {
        return input.lines()
            .map(rowParser::parse)
            .sumOf { (springs, checksum) -> recursive(checksum, "${springs}.", cached = false, ::count) }
    }

    @Answer("527570479489")
    fun part2(input: InputData): Long {
        return input.lines()
            .map(rowParser::parse)
            .sumOf { row ->
                val springs = List(5) { row.springs }.joinToString("?") + "."
                val checksum = row.checksum * 5
                recursive(checksum, springs, cached = true, ::count)
            }
    }

    private fun count(remainingGroupSizes: List<Int>, remainingSprings: String, recursion: (List<Int>, String) -> Long): Long {
        val groupSize = remainingGroupSizes[0]
        val thisSpring = remainingSprings[0]
        val withGroupStartingHere = if ((thisSpring == '#' || thisSpring == '?') && matches(remainingSprings, groupSize)) {
            if (remainingGroupSizes.size == 1) {
                if (remainingSprings.substring(groupSize + 1).none { it == '#' }) 1L else 0L
            } else {
                recursion(remainingGroupSizes.subList(1), remainingSprings.substring(groupSize + 1))
            }
        } else {
            0L
        }
        val withGroupNotStartingHere = if ((thisSpring == '.' || thisSpring == '?') && remainingSprings.length > remainingGroupSizes.sum() + remainingGroupSizes.size) {
            recursion(remainingGroupSizes, remainingSprings.substring(1))
        } else {
            0L
        }
        return withGroupStartingHere + withGroupNotStartingHere
    }

    private fun matches(remainingSprings: String, groupSize: Int): Boolean {
        return (0..<groupSize).all { remainingSprings[it] == '#' || remainingSprings[it] == '?' }
                && (remainingSprings[groupSize] == '.' || remainingSprings[groupSize] == '?')
    }

    @Pattern("([^ ]+) (.+)")
    data class Row(
        val springs: String,
        val checksum: @SplitOn(",") List<Int>
    )
}