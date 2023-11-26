package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day2Test {
    private val input = InputData.fromString(
        """
        A Y
        B X
        C Z
    """.trimIndent()
    )

    @Test
    fun part1() {
        val result = Day2.part1(input)

        assertThat(result, equalTo(15))
    }
}