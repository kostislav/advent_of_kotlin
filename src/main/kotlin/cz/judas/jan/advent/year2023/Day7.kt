package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.asMap
import cz.judas.jan.advent.characters
import cz.judas.jan.advent.multiSetOf
import cz.judas.jan.advent.splitOnOnly
import cz.judas.jan.advent.toMultiSet

object Day7 {
    fun part1(input: InputData): Int {
        val labels = mapOf(
            '2' to '2',
            '3' to '3',
            '4' to '4',
            '5' to '5',
            '6' to '6',
            '7' to '7',
            '8' to '8',
            '9' to '9',
            'T' to 'A',
            'J' to 'B',
            'Q' to 'C',
            'K' to 'D',
            'A' to 'E',
        )

        return input.lines().map { line ->
            val (hand, bid) = line.splitOnOnly(" ")
            val score = score(hand)
            Pair(score, hand.map { labels.getValue(it) }.joinToString("")) to bid.toInt()
        }
            .sortedWith(compareBy({ it.first.first }, { it.first.second } ))
            .map { it.second }
            .mapIndexed { index, bid -> (index + 1) * bid}
            .sum()
    }

    fun part2(input: InputData): Int {
        val labels = mapOf(
            '2' to '2',
            '3' to '3',
            '4' to '4',
            '5' to '5',
            '6' to '6',
            '7' to '7',
            '8' to '8',
            '9' to '9',
            'T' to 'A',
            'J' to '1',
            'Q' to 'C',
            'K' to 'D',
            'A' to 'E',
        )

        return input.lines().map { line ->
            val (hand, bid) = line.splitOnOnly(" ")
            val score = metaScore(hand, labels.keys - 'J')
            Pair(score, hand.map { labels.getValue(it) }.joinToString("")) to bid.toInt()
        }
            .sortedWith(compareBy({ it.first.first }, { it.first.second } ))
            .map { it.second }
            .mapIndexed { index, bid -> (index + 1) * bid}
            .sum()

    }

    private fun score(hand: String): Int {
        val distinctCardCounts = hand.characters().toMultiSet().asMap().values.toMultiSet()
        return when (distinctCardCounts) {
            multiSetOf(5) -> 7
            multiSetOf(4, 1) -> 6
            multiSetOf(3, 2) -> 5
            multiSetOf(3, 1, 1) -> 4
            multiSetOf(2, 2, 1) -> 3
            multiSetOf(2, 1, 1, 1) -> 2
            else -> 1
        }
    }

    private fun metaScore(hand: String, otherLabels: Set<Char>): Int {
        return if ('J' in hand) {
            otherLabels.maxOf { metaScore(hand.replaceFirst('J', it), otherLabels) }
        } else {
            score(hand)
        }
    }
}