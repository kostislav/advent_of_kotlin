package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day15Test {
    private val input = InputData.fromString("rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7")

    @Test
    fun part1() {
        assertThat(Day15.part1(input), equalTo(1320L))
    }

    @Test
    fun part2() {
        assertThat(Day15.part2(input), equalTo(145))
    }
}