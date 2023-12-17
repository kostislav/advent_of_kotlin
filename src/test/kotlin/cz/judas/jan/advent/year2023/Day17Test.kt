package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day17Test {
    private val input = InputData.fromString(
        """
            2413432311323
            3215453535623
            3255245654254
            3446585845452
            4546657867536
            1438598798454
            4457876987766
            3637877979653
            4654967986887
            4564679986453
            1224686865563
            2546548887735
            4322674655533
    """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day17.part1(input), equalTo(102))
    }

    @Nested
    inner class Part2 {
        @Test
        fun exampleFromPart1() {
            assertThat(Day17.part2(input), equalTo(94))
        }

        @Test
        fun example2() {
            val input = InputData.fromString(
                """
                111111111111
                999999999991
                999999999991
                999999999991
                999999999991
            """.trimIndent()
            )

            assertThat(Day17.part2(input), equalTo(71))
        }
    }
}