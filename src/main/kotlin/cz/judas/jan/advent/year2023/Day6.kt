package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOnPattern
import cz.judas.jan.advent.nextBiggerInt
import cz.judas.jan.advent.nextSmallerInt
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.product
import cz.judas.jan.advent.splitOnOnly
import kotlin.math.sqrt

object Day6 {
    @Answer("4403592")
    fun part1(input: InputData): Int {
        return parserFor<RaceTable>().parse(input.asString())
            .games()
            .map { game -> waysToWin(game.time.toLong(), game.distance.toLong()) }
            .product()
    }

    @Answer("38017587")
    fun part2(input: InputData): Int {
        val (time, distance) = input.lines()
            .map { line -> line.splitOnOnly(":").second.filterNot { it == ' ' }.toLong() }

        return waysToWin(time, distance)
    }

    private fun waysToWin(time: Long, maxDistance: Long): Int {
        val inflectionPoint = time / 2.0
        val limit = sqrt(inflectionPoint * inflectionPoint - maxDistance)
        return (inflectionPoint + limit).nextSmallerInt() - (inflectionPoint - limit).nextBiggerInt() + 1
    }

    @Pattern("(.*)\n(.*)")
    data class RaceTable(
        val times: TimeLine,
        val distances: DistanceLine,
    ) {
        fun games(): List<Game> {
            return times.times.zip(distances.distances).map { (time, distance) -> Game(time, distance) }
        }
    }

    @Pattern("Time:\\s+(.*)")
    data class TimeLine(val times: @SplitOnPattern("\\s+") List<Int>)

    @Pattern("Distance:\\s+(.*)")
    data class DistanceLine(val distances: @SplitOnPattern("\\s+") List<Int>)

    data class Game(val time: Int, val distance: Int)
}