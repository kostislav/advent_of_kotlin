package cz.judas.jan.advent.year2015

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day3Test {
    @Test
    fun part1() {
        assertThat(Day3.part1(InputData.fromString(">")), equalTo(2))
        assertThat(Day3.part1(InputData.fromString("^>v<")), equalTo(4))
        assertThat(Day3.part1(InputData.fromString("^v^v^v^v^v")), equalTo(2))
    }

    @Test
    fun part2() {
        assertThat(Day3.part2(InputData.fromString("^v")), equalTo(3))
        assertThat(Day3.part2(InputData.fromString("^>v<")), equalTo(3))
        assertThat(Day3.part2(InputData.fromString("^v^v^v^v^v")), equalTo(11))
    }
}
