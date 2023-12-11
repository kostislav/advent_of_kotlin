package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day11Test {
    private val input = InputData.fromString(
        """
            ...#......
            .......#..
            #.........
            ..........
            ......#...
            .#........
            .........#
            ..........
            .......#..
            #...#.....
    """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day11.part1(input), equalTo(374L))
    }

    @Test
    fun part2() {
        assertThat(Day11.expand(input, 10), equalTo(1030L))
        assertThat(Day11.expand(input, 100), equalTo(8410L))
    }
}