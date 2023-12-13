package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.subList
import cz.judas.jan.advent.times

object Day12 {
    private val rowParser = parserFor<Row>()

    @Answer("7017")
    fun part1(input: InputData): Long {
        return input.lines()
            .map(rowParser::parse)
            .sumOf { (springs, checksum) ->
                count(checksum, "${springs}.", checksum.sum() + checksum.size, mutableMapOf())
            }
    }

    @Answer("527570479489")
    fun part2(input: InputData): Long {
        return input.lines()
            .map(rowParser::parse)
            .sumOf { row ->
                val springs = List(5) { row.springs}.joinToString("?") + "."
                val checksum = row.checksum * 5
                count(checksum, springs, checksum.sum() + checksum.size, mutableMapOf())
            }
    }

    private fun count(remainingGroupSizes: List<Int>, remainingSprings: String, padding: Int, cache: MutableMap<Pair<Int, String>, Long>): Long {
        if (remainingGroupSizes.isEmpty()) {
            return if (remainingSprings.none { it == '#' }) 1L else 0L
        } else {
            val groupSize = remainingGroupSizes[0]
            var sum = 0L
            for (i in 0..(remainingSprings.length - padding)) {
                if (remainingSprings[i] == '#' || remainingSprings[i] == '?') {
                    if (
                        (0..<groupSize).all { remainingSprings[i + it] == '#' || remainingSprings[i + it] == '?' }
                        && (remainingSprings[i + groupSize] == '.' || remainingSprings[i + groupSize] == '?')
                    ) {
                        val newSprings = remainingSprings.substring(i + groupSize + 1)
                        val key = Pair(remainingGroupSizes.size - 1, newSprings)
                        if (key !in cache) {
                            cache[key] = count(
                                remainingGroupSizes.subList(1),
                                newSprings,
                                padding - groupSize - 1,
                                cache
                            )
                        }
                        sum += cache.getValue(key)
                    }
                }
                if (remainingSprings[i] == '#') {
                    break
                }
            }
            return sum
        }
    }

    @Pattern("([^ ]+) (.+)")
    data class Row(
        val springs: String,
        val checksum: @SplitOn(",") List<Int>
    )
}