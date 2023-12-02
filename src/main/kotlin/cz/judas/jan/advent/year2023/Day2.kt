package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.parse
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.product
import cz.judas.jan.advent.toMap
import cz.judas.jan.advent.toMultiMap

object Day2 {
    private val parser = parserFor<Game>()

    fun part1(input: InputData): Int {
        val maxCubes = mapOf(
            Color.RED to 12,
            Color.GREEN to 13,
            Color.BLUE to 14,
        )

        return input.lines()
            .sumOf { line ->
                val game = line.parse(parser)
                val isPossible = game.sets
                    .flatten()
                    .all { reveal -> reveal.number <= maxCubes.getValue(reveal.color) }
                if (isPossible) game.number else 0
            }
    }

    fun part2(input: InputData): Int {
        return input.lines()
            .sumOf { line ->
                val game = line.parse(parser)
                game.sets
                    .flatten()
                    .map { reveal -> reveal.color to reveal.number }
                    .toMultiMap()
                    .toMap { numbers -> numbers.max() }
                    .values
                    .product()
            }
    }

    @Pattern("Game (\\d+): (.+)")
    data class Game(
        val number: Int,
        val sets: @SplitOn("; ") List<@SplitOn(", ") List<Reveal>>,
    ) {
        @Pattern("(\\d+) ([a-z]+)")
        data class Reveal(
            val number: Int,
            val color: Color,
        )
    }

    enum class Color {
        RED, GREEN, BLUE
    }
}