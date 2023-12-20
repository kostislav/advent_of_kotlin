package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day20Test {
    @Nested
    inner class Part1 {
        @Test
        fun example1() {
            val input = InputData.fromString(
                """
                broadcaster -> a, b, c
                %a -> b
                %b -> c
                %c -> inv
                &inv -> a
            """.trimIndent()
            )

            assertThat(Day20.part1(input), equalTo(32000000L))
        }

        @Test
        fun example2() {
            val input = InputData.fromString(
                """
                broadcaster -> a
                %a -> inv, con
                &inv -> b
                %b -> con
                &con -> output
            """.trimIndent()
            )

            assertThat(Day20.part1(input), equalTo(11687500L))
        }
    }
}