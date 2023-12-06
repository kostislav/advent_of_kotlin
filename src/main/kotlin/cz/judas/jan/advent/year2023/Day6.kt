package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOnPattern
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.product
import cz.judas.jan.advent.splitOnOnly

object Day6 {
    fun part1(input: InputData): Int {
        return parserFor<RaceTable>().parse(input.asString())
            .games()
            .map { game ->
                (1..<game.time)
                    .map { it * (game.time - it) }
                    .filter { it > game.distance }
                    .size
            }
            .product()
    }

    fun part2(input: InputData): Int {
        val (time, distance) = input.lines()
            .map { it.splitOnOnly(":").second.filterNot { it == ' ' }.toLong() }

        return (1..<time)
            .asSequence()
            .map { it * (time - it) }
            .filter { it > distance }
            .count()
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