package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day16Test {
    private val input = InputData.fromString(
        """
            .|...\....
            |.-.\.....
            .....|-...
            ........|.
            ..........
            .........\
            ..../.\\..
            .-.-/..|..
            .|....-|.\
            ..//.|....
    """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day16.part1(input), equalTo(46))
    }

    @Test
    fun part2() {
        assertThat(Day16.part2(input), equalTo(51))
    }
}