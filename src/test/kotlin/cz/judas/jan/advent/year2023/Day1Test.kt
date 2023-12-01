package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day1Test {
    @Test
    fun part1() {
        val input = InputData.fromString(
            """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
    """.trimIndent()
        )

        val result = Day1.part1(input)

        assertThat(result, equalTo(142))
    }

    @Test
    fun part2() {
        val input = InputData.fromString(
            """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixtee
    """.trimIndent()
        )

        val result = Day1.part2(input)

        assertThat(result, equalTo(281))
    }
}