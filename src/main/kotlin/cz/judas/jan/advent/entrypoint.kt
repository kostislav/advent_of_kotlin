package cz.judas.jan.advent

import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

data class PuzzleResult(val answer: Any, val computationTime: Long)

fun run(year: Int, day: Int, part: Int, submit: Boolean): PuzzleResult {
    val dayClass = Class.forName("cz.judas.jan.advent.year${year}.Day${day}")
    val partMethod = dayClass.getMethod("part${part}", InputData::class.java)
    val dayInstance = dayClass.getField("INSTANCE").get(null)
    val lookup = MethodHandles.lookup()
    val partMethodHandle = lookup.findVirtual(dayClass, partMethod.name, MethodType.methodType(partMethod.returnType, InputData::class.java))
    val inputFetcher = InputFetcher()
    val inputData = inputFetcher.get(year, day)
    val startTime = System.currentTimeMillis()
    val answer = partMethodHandle.invoke(dayInstance, inputData)
    val endTime = System.currentTimeMillis()
    if (submit) {
        AdventOfCodeApi().submitAnswer(year, day, part, answer)
    }
    return PuzzleResult(answer, endTime - startTime)
}

fun main() {
    val result = run(year = 2023, day = 1, part = 1, submit = false)
    println()
    println()
    println(result.answer)
    println()
    println()
    println("Took ${result.computationTime} ms")
}