package cz.judas.jan.advent

fun part1(input: InputData) {
    val largestElf = input.transformLines { lines ->
        lines
            .splitOn { it.isEmpty() }
            .map { chunk -> chunk.map { it.toInt() }.sum() }
            .max()
    }

    println(largestElf)
}


fun part2(input: InputData) {
    val largest3Elves = input.transformLines { lines ->
        lines
            .splitOn { it.isEmpty() }
            .map { chunk -> chunk.map { it.toInt() }.sum() }
            .maxN(3)
            .sum()
    }

    println(largest3Elves)
}

fun main() = part2(InputData.forDay(1))