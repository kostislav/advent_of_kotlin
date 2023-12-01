package cz.judas.jan.advent

fun run(year: Int, day: Int, part: Int, submit: Boolean): Any {
    val dayClass = Class.forName("cz.judas.jan.advent.year${year}.Day${day}")
    val partMethod = dayClass.getMethod("part${part}", InputData::class.java)
    val dayInstance = dayClass.getField("INSTANCE").get(null)
    val inputFetcher = InputFetcher()
    val answer = partMethod.invoke(dayInstance, inputFetcher.get(year, day))
    if (submit) {
        AdventOfCodeApi().submitAnswer(year, day, part, answer)
    }
    return answer
}

fun main() = println(run(year = 2023, day = 1, part = 2, submit = false))