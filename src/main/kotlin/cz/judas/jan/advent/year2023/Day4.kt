package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.parse
import cz.judas.jan.advent.parserFor

object Day4 {
    private val parser = parserFor<Card>()

    fun part1(input: InputData): Int {
        return input.lines()
            .map { it.parse(parser).numMatches() }
            .filterNot { it == 0 }
            .sumOf { 1 shl it - 1 }
    }

    fun part2(input: InputData): Int {
        return input.lines()
            .reversed()
            .fold(emptyMap<Int, Int>()) { matchingNumberCount, line ->
                val card = line.parse(parser)
                val numMatches = card.numMatches()
                val wonCards = IntRange(card.number + 1, card.number + numMatches)
                    .sumOf { matchingNumberCount[it] ?: 0 }
                matchingNumberCount + mapOf(card.number to wonCards + 1)
            }
            .values
            .sum()
    }

    @Pattern("Card +(\\d+): (.+) \\| (.+)")
    data class Card(
        val number: Int,
        val winningNumbers: List<@Pattern("\\d+") Int>,
        val numbersIHave: List<@Pattern("\\d+") Int>,
    ) {
        fun numMatches(): Int = numbersIHave.toSet().intersect(winningNumbers.toSet()).size
    }
}