package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day7Test {
    private val input = InputData.fromString(
        """
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
    """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day7.part1(input), equalTo(6440))
    }

    @Test
    fun part2() {
        assertThat(Day7.part2(input), equalTo(5905))
    }
}