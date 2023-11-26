package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day3Test {
    private val input = InputData.fromString(
        """
        vJrwpWtwJgWrhcsFMMfFFhFp
        jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
        PmmdzqPrVvPwwTWBwg
        wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
        ttgJtRGJQctTZtZT
        CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent()
    )

    @Test
    fun part1() {
        val result = Day3.part1(input)

        assertThat(result, equalTo(157))
    }

    @Test
    fun part2() {
//        val result = Day2.part2(input)
//
//        assertThat(result, equalTo(12))
    }
}