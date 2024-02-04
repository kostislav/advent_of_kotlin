package cz.judas.jan.advent

data class PuzzleResult(val answer: Any, val computationTime: Long)

fun run(year: Int, day: Int, part: Int, submit: Boolean): PuzzleResult {
    val dayClass = Class.forName("cz.judas.jan.advent.year${year}.Day${day}")
    val partMethod = dayClass.getMethod("part${part}", InputData::class.java)
    val dayInstance = dayClass.getField("INSTANCE").get(null)
    val inputFetcher = InputFetcher()
    val inputData = inputFetcher.get(year, day)
    val startTime = System.currentTimeMillis()
    val answer = partMethod.invoke(dayInstance, inputData)
    val endTime = System.currentTimeMillis()
    if (submit) {
        AdventOfCodeApi().submitAnswer(year, day, part, answer)
    }
    return PuzzleResult(answer, endTime - startTime)
}

fun main() {
    val result = run(year = 2015, day = 4, part = 2, submit = false)
    println()
    println()
    println(result.answer)
    println()
    println()
    println("Took ${result.computationTime} ms")
}