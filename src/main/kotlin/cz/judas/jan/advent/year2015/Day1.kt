package cz.judas.jan.advent.year2015

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.pairWithIndex
import cz.judas.jan.advent.scanSecond
import cz.judas.jan.advent.translate

object Day1 {
    private val options = mapOf(
        '(' to 1,
        ')' to -1
    )

    fun part1(input: InputData): Int {
        return input.asString().translate(options).sum()
    }

    fun part2(input: InputData): Int {
        return input.asString()
            .translate(options)
            .pairWithIndex(startingIndex = 1)
            .scanSecond(0, Int::plus)
            .find { it.second == -1 }!!
            .first
    }
}