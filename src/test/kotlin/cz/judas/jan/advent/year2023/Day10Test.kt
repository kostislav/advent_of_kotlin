package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day10Test {
    @Nested
    inner class Part1 {
        @Test
        fun example1() {
            val input = InputData.fromString(
                """
                -L|F7
                7S-7|
                L|7||
                -L-J|
                L|-JF
            """.trimIndent()
            )

            assertThat(Day10.part1(input), equalTo(4))
        }

        @Test
        fun example2() {
            val input = InputData.fromString(
                """
                7-F7-
                .FJ|7
                SJLL7
                |F--J
                LJ.LJ
            """.trimIndent()
            )

            assertThat(Day10.part1(input), equalTo(8))
        }
    }

    @Test
    fun part2() {
        val input = InputData.fromString(
            """
            FF7FSF7F7F7F7F7F---7
            L|LJ||||||||||||F--J
            FL-7LJLJ||||||LJL-77
            F--JF--7||LJLJ7F7FJ-
            L---JF-JLJ.||-FJLJJ7
            |F|F-JF---7F7-L7L|7|
            |FFJF7L7F-JF7|JL---7
            7-L-JL7||F7|L7F-7F7|
            L.L7LFJ|||||FJL7||LJ
            L7JLJL-JLJLJL--JLJ.L
            """.trimIndent()
        )

        assertThat(Day10.part2(input), equalTo(10))
    }
}