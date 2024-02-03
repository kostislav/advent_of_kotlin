package cz.judas.jan.advent.year2015

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day2Test {
    @Test
    fun part1() {
        assertThat(Day2.part1(Day2.Present(2, 3, 4)), equalTo(58))
        assertThat(Day2.part1(Day2.Present(1, 1, 10)), equalTo(43))
    }

    @Test
    fun part2() {
        assertThat(Day2.part2(Day2.Present(2, 3, 4)), equalTo(34))
        assertThat(Day2.part2(Day2.Present(1, 1, 10)), equalTo(14))
    }
}
