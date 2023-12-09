package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day9Test {
    private val input = InputData.fromString(
        """
            0 3 6 9 12 15
            1 3 6 10 15 21
            10 13 16 21 30 45
    """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day9.part1(input), equalTo(114))
    }

    @Test
    fun part2() {
        assertThat(Day9.part2(input), equalTo(2))
    }
}