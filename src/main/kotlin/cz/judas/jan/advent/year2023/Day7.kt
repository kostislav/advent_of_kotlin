package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.LexicographicalListComparator
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.asMap
import cz.judas.jan.advent.associateToIndex
import cz.judas.jan.advent.mapIndexedFrom
import cz.judas.jan.advent.multiSetOf
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.replace
import cz.judas.jan.advent.toMultiSet
import cz.judas.jan.advent.year2023.Day7.CamelCardScoring.score

object Day7 {
    private val comparator =
        compareBy<Pair<Pair<Int, List<Int>>, Int>> { it.first.first }
            .then(compareBy(LexicographicalListComparator.natural()) { it.first.second })

    private val parser = parserFor<Game>()

    @Answer("245794640")
    fun part1(input: InputData): Int {
        val labelIndexes = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A').associateToIndex()

        return input.lines()
            .map { line ->
                val game = parser.parse(line)
                val score = score(game.hand)
                Pair(score, game.hand.map { labelIndexes.getValue(it) }) to game.bid
            }
            .let(::totalWinnings)
    }

    @Answer("247899149")
    fun part2(input: InputData): Int {
        val labelIndexes = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A').associateToIndex()
        val nonJokers = labelIndexes.keys - 'J'

        return input.lines()
            .map { line ->
                val game = parser.parse(line)
                val score = metaScore(game.hand, nonJokers)
                Pair(score, game.hand.map { labelIndexes.getValue(it) }) to game.bid
            }
            .let(::totalWinnings)
    }

    private fun totalWinnings(games: List<Pair<Pair<Int, List<Int>>, Int>>): Int {
        return games
            .sortedWith(comparator)
            .map { it.second }
            .mapIndexedFrom(1) { index, bid -> index * bid }
            .sum()
    }

    private fun metaScore(hand: List<Char>, otherLabels: Set<Char>): Int {
        return if ('J' in hand) {
            otherLabels.maxOf { score(hand.replace('J', it)) }
        } else {
            score(hand)
        }
    }

    @Pattern("(.+) (\\d+)")
    data class Game(
        val hand: List<Char>,
        val bid: Int
    )

    object CamelCardScoring {
        private val fiveOfAKind = multiSetOf(5)
        private val fourOfAKind = multiSetOf(4, 1)
        private val fullHouse = multiSetOf(3, 2)
        private val threeOfAKind = multiSetOf(3, 1, 1)
        private val twoPairs = multiSetOf(2, 2, 1)
        private val onePair = multiSetOf(2, 1, 1, 1)

        fun score(hand: List<Char>): Int {
            val distinctCardCounts = hand.toMultiSet().asMap().values.toMultiSet()
            return when (distinctCardCounts) {
                fiveOfAKind -> 7
                fourOfAKind -> 6
                fullHouse -> 5
                threeOfAKind -> 4
                twoPairs -> 3
                onePair -> 2
                else -> 1
            }
        }
    }
}