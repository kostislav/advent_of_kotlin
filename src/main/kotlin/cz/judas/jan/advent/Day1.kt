package cz.judas.jan.advent

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

fun main() = println(part2(InputData.forDay(1)))