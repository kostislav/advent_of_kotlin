package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day12Test {
    private val input = InputData.fromString(
        """
            ???.### 1,1,3
            .??..??...?##. 1,1,3
            ?#?#?#?#?#?#?#? 1,3,1,6
            ????.#...#... 4,1,1
            ????.######..#####. 1,6,5
            ?###???????? 3,2,1
    """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day12.part1(input), equalTo(21))
    }

    @Test
    fun part2() {
        assertThat(Day12.part2(input), equalTo(525152L))
    }
}