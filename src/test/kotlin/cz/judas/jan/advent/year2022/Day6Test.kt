package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day6Test {
    @Test
    fun part1() {
        assertThat(Day6.part1(InputData.fromString("bvwbjplbgvbhsrlpgdmjqwftvncz")), equalTo(5))
        assertThat(Day6.part1(InputData.fromString("nppdvjthqldpwncqszvftbrmjlhg")), equalTo(6))
        assertThat(Day6.part1(InputData.fromString("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg")), equalTo(10))
        assertThat(Day6.part1(InputData.fromString("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw")), equalTo(11))
    }
}