package cz.judas.jan.advent

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day1Test {
    private val input = InputData.fromString("""
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
    """.trimIndent())

    @Test
    fun part1Works() {
        val result = part1(input)

        assertThat(result, equalTo(24000))
    }

    @Test
    fun part2Works() {
        val result = part2(input)

        assertThat(result, equalTo(45000))
    }
}