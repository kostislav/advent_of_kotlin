package cz.judas.jan.advent.year2023

import com.google.common.collect.Range
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOnPattern
import cz.judas.jan.advent.enclosedLongRange
import cz.judas.jan.advent.length
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
        return Range.open(inflectionPoint - limit, inflectionPoint + limit).enclosedLongRange().length().toInt()
    }

    @Pattern("Time:\\s+(.*)\nDistance:\\s+(.*)")
    data class RaceTable(
        val times: @SplitOnPattern("\\s+") List<Int>,
        val distances: @SplitOnPattern("\\s+") List<Int>,
    ) {
        fun games(): List<Game> {
            return times.zip(distances).map { (time, distance) -> Game(time, distance) }
        }
    }

    data class Game(val time: Int, val distance: Int)
}