package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOnPattern
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.product
import cz.judas.jan.advent.splitOnOnly
import kotlin.math.ceil
import kotlin.math.floor
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
        return nextSmallerInt(inflectionPoint + limit) - nextBiggerInt(inflectionPoint - limit) + 1
    }

    fun nextSmallerInt(value: Double): Int {
        val floor = floor(value)
        return if (floor == value) {
            floor - 1
        } else {
            floor
        }.toInt()
    }

    fun nextBiggerInt(value: Double): Int {
        val ceil = ceil(value)
        return if (ceil == value) {
            ceil + 1
        } else {
            ceil
        }.toInt()
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