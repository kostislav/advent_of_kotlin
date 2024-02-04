package cz.judas.jan.advent.year2015

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day4Test {
    @Test
    fun part1() {
        assertThat(Day4.part1(InputData.fromString("abcdef")), equalTo(609043))
        assertThat(Day4.part1(InputData.fromString("pqrstuv")), equalTo(1048970))
    }
}
