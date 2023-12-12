package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.subList
import cz.judas.jan.advent.times

object Day12 {
    private val rowParser = parserFor<Row>()

    fun part1(input: InputData): Int {
        return input.lines()
            .map(rowParser::parse)
            .sumOf { (springs, checksum) ->
                val numBroken = springs.count { it =='?' }
                val missingSprings = checksum.sum() - springs.count { it == '#' }
                val powerSet = 1 shl numBroken
                (0..<powerSet).asSequence()
                    .filter { it.countOneBits() == missingSprings && matches(springs, checksum, it) }
                    .count()
            }
    }

    fun part2(input: InputData): Long {
        return input.lines()
            .map(rowParser::parse)
            .sumOf { row ->
                val springs = buildList {
                    addAll(row.springs)
                    add('?')
                    addAll(row.springs)
                    add('?')
                    addAll(row.springs)
                    add('?')
                    addAll(row.springs)
                    add('?')
                    addAll(row.springs)
                    add('.')
                }
                val checksum = row.checksum * 5
                count(checksum, springs.joinToString(""), checksum.sum() + checksum.size, mutableMapOf())
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


    private fun matches(springs: List<Char>, checksum: List<Int>, combination: Int): Boolean {
        val checksumIterator = checksum.iterator()
        var currentGroupLength = 0
        var brokenSpringIndex = 1
        for (spring in springs) {
            val realSpring = if (spring == '?') {
                val replacement = if (combination and brokenSpringIndex != 0) '#' else '.'
                brokenSpringIndex = brokenSpringIndex shl 1
                replacement
            } else {
                spring
            }

            if (realSpring == '#') {
                currentGroupLength += 1
            } else if (currentGroupLength > 0) {
                if (checksumIterator.hasNext() && currentGroupLength == checksumIterator.next()) {
                    currentGroupLength = 0
                } else {
                    return false
                }
            }
        }
        return (currentGroupLength == 0 && !checksumIterator.hasNext()) || checksumIterator.next() == currentGroupLength
    }


    @Pattern("([^ ]+) (.+)")
    data class Row(
        val springs: List<Char>,
        val checksum: @SplitOn(",") List<Int>
    )
}