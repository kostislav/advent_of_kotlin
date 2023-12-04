package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOnPattern
import cz.judas.jan.advent.parse
import cz.judas.jan.advent.parserFor

object Day4 {
    private val parser = parserFor<Card>()

    @Answer("28538")
    fun part1(input: InputData): Int {
        return input.lines()
            .map { it.parse(parser).numMatches() }
            .filterNot { it == 0 }
            .sumOf { 1 shl it - 1 }
    }

    @Answer("9425061")
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
        val winningNumbers: @SplitOnPattern("\\s+") List<Int>,
        val numbersIHave: @SplitOnPattern("\\s+") List<Int>,
    ) {
        fun numMatches(): Int = numbersIHave.toSet().intersect(winningNumbers.toSet()).size
    }
}