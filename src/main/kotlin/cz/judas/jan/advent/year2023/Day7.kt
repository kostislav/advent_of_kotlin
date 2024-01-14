package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.LexicographicalListComparator
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.associateToIndex
import cz.judas.jan.advent.histogram
import cz.judas.jan.advent.mapIndexedFrom
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.subList

object Day7 {
    private val parser = parserFor<Game>()

    @Answer("245794640")
    fun part1(input: InputData): Int {
        val labelIndexes = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A').associateToIndex()

        return input.lines()
            .map { line ->
                val game = parser.parse(line)
                val score = game.hand.histogram().values.sorted().reversed()
                AnalyzedHand(score, game.hand.map { labelIndexes.getValue(it) }, game.bid)
            }
            .let(::totalWinnings)
    }

    @Answer("247899149")
    fun part2(input: InputData): Int {
        val labelIndexes = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A').associateToIndex()

        return input.lines()
            .map { line ->
                val game = parser.parse(line)
                val scoreWithoutJokers = game.hand.filterNot { it == 'J' }.histogram().values.sorted().reversed()
                val numJokers = game.hand.count { it == 'J' }
                val scoreWithJokers = if (scoreWithoutJokers.isNotEmpty()) {
                    listOf(scoreWithoutJokers[0] + numJokers) + scoreWithoutJokers.subList(1)
                } else {
                    listOf(numJokers)
                }
                AnalyzedHand(scoreWithJokers, game.hand.map { labelIndexes.getValue(it) }, game.bid)
            }
            .let(::totalWinnings)
    }

    private fun totalWinnings(games: List<AnalyzedHand>): Int {
        return games
            .sortedWith(
                Comparator.comparing(AnalyzedHand::valueHistogram, LexicographicalListComparator.natural())
                    .then(Comparator.comparing(AnalyzedHand::cards, LexicographicalListComparator.natural()))
            )
            .mapIndexedFrom(1) { index, hand -> index * hand.bid }
            .sum()
    }

    @Pattern("(.+) (\\d+)")
    data class Game(
        val hand: List<Char>,
        val bid: Int
    )

    data class AnalyzedHand(
        val valueHistogram: List<Int>,
        val cards: List<Int>,
        val bid: Int
    )
}