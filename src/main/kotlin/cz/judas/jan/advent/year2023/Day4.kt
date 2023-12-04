package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.parse
import cz.judas.jan.advent.parserFor

object Day4 {
    fun part1(input: InputData): Int {
        val parser = parserFor<Card>()

        return input.lines()
            .map { line ->
                val card = line.parse(parser)
                card.numbersIHave.toSet().intersect(card.winningNumbers.toSet())
            }
            .filterNot { it.isEmpty() }
            .sumOf { 1 shl it.size - 1 }
    }

    fun part2(input: InputData): Int {
        val parser = parserFor<Card>()
        val matchingNumberCount = mutableMapOf<Int, Int>()

        input.lines()
            .reversed()
            .forEach { line ->
                val card = line.parse(parser)
                val numMatches = card.numbersIHave.toSet().intersect(card.winningNumbers.toSet()).size
                val wonCards = IntRange(card.number + 1, card.number + numMatches)
                    .sumOf { matchingNumberCount[it] ?: 0 }
                matchingNumberCount[card.number] = wonCards + 1
            }

        return matchingNumberCount.values.sum()
    }

    @Pattern("Card +(\\d+): (.+) \\| (.+)")
    data class Card(
        val number: Int,
        val winningNumbers: List<@Pattern("\\d+") Int>,
        val numbersIHave: List<@Pattern("\\d+") Int>,
    )
}