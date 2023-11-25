package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.maxN
import cz.judas.jan.advent.splitOn


object Day1 {
    fun part1(input: InputData): Int {
        return input.lines()
            .splitOn { it.isEmpty() }
            .maxOfOrNull { chunk -> chunk.sumOf { it.toInt() } }!!
    }


    fun part2(input: InputData): Int {
        return input.lines()
            .splitOn { it.isEmpty() }
            .map { chunk -> chunk.sumOf { it.toInt() } }
            .maxN(3)
            .sum()
    }
}

fun main() = println(Day1.part2(InputData.forDay(2022, 1)))