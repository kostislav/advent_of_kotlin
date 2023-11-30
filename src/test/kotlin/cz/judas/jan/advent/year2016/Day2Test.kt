package cz.judas.jan.advent.year2016

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day2Test {
    private val input = InputData.fromString(
        """
        ULL
        RRDDD
        LURDL
        UUUUD
    """.trimIndent()
    )

    @Test
    fun part1() {
        val result = Day2.part1(input)

        assertThat(result, equalTo("1985"))
    }

    @Test
    fun part2() {
//        val result = Day2.part2(input)
//
//        assertThat(result, equalTo(45000))
    }
}