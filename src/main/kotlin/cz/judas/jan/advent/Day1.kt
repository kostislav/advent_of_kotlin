package cz.judas.jan.advent

fun part1(input: InputData) {
    val largestElf = input.lines()
        .splitOn { it.isEmpty() }
        .maxOfOrNull { chunk -> chunk.sumOf { it.toInt() } }

    println(largestElf)
}


fun part2(input: InputData) {
    val largest3Elves = input.lines()
        .splitOn { it.isEmpty() }
        .map { chunk -> chunk.sumOf { it.toInt() } }
        .maxN(3)
        .sum()

    println(largest3Elves)
}

fun main() = part2(InputData.forDay(1))