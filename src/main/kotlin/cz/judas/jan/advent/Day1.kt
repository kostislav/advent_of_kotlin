package cz.judas.jan.advent


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

fun main() = println(Day1.part2(InputData.forDay(1)))