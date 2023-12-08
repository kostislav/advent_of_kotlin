package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day8Test {
    @Nested
    inner class Part1 {
        @Test
        fun example1() {
            val input = InputData.fromString(
                """
            RL
    
            AAA = (BBB, CCC)
            BBB = (DDD, EEE)
            CCC = (ZZZ, GGG)
            DDD = (DDD, DDD)
            EEE = (EEE, EEE)
            GGG = (GGG, GGG)
            ZZZ = (ZZZ, ZZZ)
        """.trimIndent()
            )

            assertThat(Day8.part1(input), equalTo(2))
        }

        @Test
        fun example2() {
            val input = InputData.fromString(
                """
                LLR
    
                AAA = (BBB, BBB)
                BBB = (AAA, ZZZ)
                ZZZ = (ZZZ, ZZZ)
        """.trimIndent()
            )

            assertThat(Day8.part1(input), equalTo(6))
        }
    }

    @Test
    fun part2() {
        val input = InputData.fromString(
            """
            LR
    
            11A = (11B, XXX)
            11B = (XXX, 11Z)
            11Z = (11B, XXX)
            22A = (22B, XXX)
            22B = (22C, 22C)
            22C = (22Z, 22Z)
            22Z = (22B, 22B)
            XXX = (XXX, XXX)
        """.trimIndent()
        )

        assertThat(Day8.part2(input), equalTo(6))
    }
}