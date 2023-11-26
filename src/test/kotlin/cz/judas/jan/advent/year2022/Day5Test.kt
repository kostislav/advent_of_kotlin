package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day5Test {
    private val input = InputData.fromString(
        """
            [D]    
        [N] [C]    
        [Z] [M] [P]
         1   2   3 
        
        move 1 from 2 to 1
        move 3 from 1 to 3
        move 2 from 2 to 1
        move 1 from 1 to 2
    """.trimIndent()
    )

    @Test
    fun part1() {
        val result = Day5.part1(input)

        assertThat(result, equalTo("CMZ"))
    }

    @Test
    fun part2() {
//        val result = Day5.part2(input)
//
//        assertThat(result, equalTo(4))
    }
}