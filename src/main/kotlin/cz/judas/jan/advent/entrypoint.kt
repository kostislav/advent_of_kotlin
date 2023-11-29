package cz.judas.jan.advent

fun run(year: Int, day: Int, part: Int): Any {
    val dayClass = Class.forName("cz.judas.jan.advent.year${year}.Day${day}")
    val partMethod = dayClass.getMethod("part${part}", InputData::class.java)
    val dayInstance = dayClass.getField("INSTANCE").get(null)
    val inputFetcher = InputFetcher()
    return partMethod.invoke(dayInstance, inputFetcher.get(year, day))
}

fun main() = println(run(year = 2022, day = 6, part = 1))