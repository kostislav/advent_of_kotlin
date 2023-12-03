package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day3Test {
    private val input = InputData.fromString(
        """
            467..114..
            ...*......
            ..35..633.
            ......#...
            617*......
            .....+.58.
            ..592.....
            ......755.
            ...${'$'}.*....
            .664.598..
    """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day3.part1(input), equalTo(4361))
    }

    @Test
    fun part2() {
        assertThat(Day3.part2(input), equalTo(467835))
    }
}