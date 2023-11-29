package cz.judas.jan.advent.year2015

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day25Test {
    @Test
    fun part1() {
        assertThat(Day25.codeFor(1, 1), equalTo(20151125))
        assertThat(Day25.codeFor(6, 2), equalTo(6796745))
    }

    @Test
    fun returnsCorrectIndex() {
        assertThat(Day25.indexFor(1, 1), equalTo(1))
        assertThat(Day25.indexFor(3, 3), equalTo(13))
        assertThat(Day25.indexFor(6, 1), equalTo(16))
        assertThat(Day25.indexFor(1, 6), equalTo(21))
    }
}