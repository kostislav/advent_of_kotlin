package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day4Test {
    private val input = InputData.fromString(
        """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent()
    )

    @Test
    fun part1() {
        val result = Day4.part1(input)

        assertThat(result, equalTo(2))
    }

    @Test
    fun part2() {
        val result = Day4.part2(input)

        assertThat(result, equalTo(4))
    }
}