package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day21Test {
    private val input = InputData.fromString(
        """
            ...........
            .....###.#.
            .###.##..#.
            ..#.#...#..
            ....#.#....
            .##..S####.
            .##..#...#.
            .......##..
            .##.#.####.
            .##..##.##.
            ...........
    """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day21.part1(input, 6), equalTo(16))
    }

    @Test
    fun part2() {
        assertThat(Day21.part2(input, 6), equalTo(16L))
        assertThat(Day21.part2(input, 10), equalTo(50L))
        assertThat(Day21.part2(input, 50), equalTo(1594L))
        assertThat(Day21.part2(input, 100), equalTo(6536L))
        assertThat(Day21.part2(input, 500), equalTo(167004L))
        assertThat(Day21.part2(input, 1000), equalTo(668697L))
        assertThat(Day21.part2(input, 5000), equalTo(16733044L))
    }
}