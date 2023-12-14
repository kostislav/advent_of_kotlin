package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day14Test {
    private val input = InputData.fromString(
        """
            O....#....
            O.OO#....#
            .....##...
            OO.#O....O
            .O.....O#.
            O.#..O.#.#
            ..O..#O..O
            .......O..
            #....###..
            #OO..#....
    """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day14.part1(input), equalTo(136))
    }

    @Test
    fun part2() {
        assertThat(Day14.part2(input), equalTo(64))
    }
}