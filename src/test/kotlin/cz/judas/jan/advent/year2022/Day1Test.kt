package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

class Day1Test {
    private val input = InputData.fromString(
        """
        1000
        2000
        3000

        4000

        5000
        6000

        7000
        8000
        9000

        10000
    """.trimIndent()
    )

    @Test
    fun part1() {
        val result = Day1.part1(input)

        MatcherAssert.assertThat(result, Matchers.equalTo(24000))
    }

    @Test
    fun part2() {
        val result = Day1.part2(input)

        MatcherAssert.assertThat(result, Matchers.equalTo(45000))
    }
}