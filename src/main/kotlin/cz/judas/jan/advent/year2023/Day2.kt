package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.product
import cz.judas.jan.advent.toMergedMap

object Day2 {
    private val parser = parserFor<Game>()

    @Answer("2156")
    fun part1(input: InputData): Int {
        val maxCubes = mapOf(
            Color.RED to 12,
            Color.GREEN to 13,
            Color.BLUE to 14,
        )

        return input.lines()
            .sumOf { line ->
                val game = parser.parse(line)
                val isPossible = game.sets
                    .flatten()
                    .all { reveal -> reveal.number <= maxCubes.getValue(reveal.color) }
                if (isPossible) game.number else 0
            }
    }

    @Answer("66909")
    fun part2(input: InputData): Int {
        return input.lines()
            .sumOf { line ->
                val game = parser.parse(line)
                game.sets
                    .flatten()
                    .map { reveal -> reveal.color to reveal.number }
                    .toMergedMap { numbers -> numbers.max() }
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