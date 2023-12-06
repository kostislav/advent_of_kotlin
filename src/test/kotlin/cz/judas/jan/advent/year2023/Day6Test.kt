package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day6Test {
    private val input = InputData.fromString(
        """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day6.part1(input), equalTo(288))
    }

    @Test
    fun part2() {
        assertThat(Day6.part2(input), equalTo(71503))
    }
}