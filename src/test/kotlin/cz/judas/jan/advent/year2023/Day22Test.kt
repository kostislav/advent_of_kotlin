package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day22Test {
    private val input = InputData.fromString(
        """
        1,0,1~1,2,1
        0,0,2~2,0,2
        0,2,3~2,2,3
        0,0,4~0,2,4
        2,0,5~2,2,5
        0,1,6~2,1,6
        1,1,8~1,1,9
    """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day22.part1(input), equalTo(5))
    }

    @Test
    fun part2() {
        assertThat(Day22.part2(input), equalTo(7))
    }
}