package cz.judas.jan.advent

fun day1(input: InputData) {
    val total = input.transformLines { lines ->
        lines
            .splitOn { it.isEmpty() }
            .map { chunk -> chunk.map { it.toInt() }.sum() }
            .max()
    }

    println(total)
}

fun main() = day1(InputData.forDay(1))