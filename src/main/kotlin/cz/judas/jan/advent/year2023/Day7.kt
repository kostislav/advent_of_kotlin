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
                Pair(score, game.hand.map { labelIndexes.getValue(it) }) to game.bid
            }
            .let(::totalWinnings)
    }

    @Answer("247899149")
    fun part2(input: InputData): Int {
        val labelIndexes = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A').associateToIndex()

        return input.lines()
            .map { line ->
                val game = parser.parse(line)
                val score = game.hand.filterNot { it == 'J' }.histogram().values.sorted().reversed()
                val numJokers = game.hand.count { it == 'J' }
                val improvedScore = if (score.isNotEmpty()) {
                    listOf(score[0] + numJokers) + score.subList(1)
                } else {
                    listOf(numJokers)
                }
                Pair(improvedScore, game.hand.map { labelIndexes.getValue(it) }) to game.bid
            }
            .let(::totalWinnings)
    }

    private fun totalWinnings(games: List<Pair<Pair<List<Int>, List<Int>>, Int>>): Int {
        return games
            .sortedWith(HandComparator)
            .map { it.second }
            .mapIndexedFrom(1) { index, bid -> index * bid }
            .sum()
    }

    @Pattern("(.+) (\\d+)")
    data class Game(
        val hand: List<Char>,
        val bid: Int
    )

    object HandComparator: Comparator<Pair<Pair<List<Int>, List<Int>>, Int>> {
        private val intListComparator = LexicographicalListComparator.natural<Int>()

        override fun compare(o1: Pair<Pair<List<Int>, List<Int>>, Int>, o2: Pair<Pair<List<Int>, List<Int>>, Int>): Int {
            val primary = intListComparator.compare(o1.first.first, o2.first.first)
            return if (primary != 0) primary else intListComparator.compare(o1.first.second, o2.first.second)
        }
    }
}