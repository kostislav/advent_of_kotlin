package cz.judas.jan.advent.year2015

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day1Test {
    @Test
    fun part1() {
        assertThat(Day1.part1(InputData.fromString("(())")), equalTo(0))
        assertThat(Day1.part1(InputData.fromString("()()")), equalTo(0))
        assertThat(Day1.part1(InputData.fromString("(((")), equalTo(3))
        assertThat(Day1.part1(InputData.fromString("(()(()(")), equalTo(3))
        assertThat(Day1.part1(InputData.fromString("))(((((")), equalTo(3))
        assertThat(Day1.part1(InputData.fromString("())")), equalTo(-1))
        assertThat(Day1.part1(InputData.fromString("))(")), equalTo(-1))
        assertThat(Day1.part1(InputData.fromString(")))")), equalTo(-3))
        assertThat(Day1.part1(InputData.fromString(")())())")), equalTo(-3))
    }

    @Test
    fun part2() {
        assertThat(Day1.part2(InputData.fromString(")")), equalTo(1))
        assertThat(Day1.part2(InputData.fromString("()())")), equalTo(5))
    }
}