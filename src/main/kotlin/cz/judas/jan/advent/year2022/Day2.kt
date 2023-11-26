package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.cartesianProduct

object Day2 {
    fun part1(input: InputData): Int {
        val elfShapeNames = mapOf(
            Shape.ROCK to "A",
            Shape.PAPER to "B",
            Shape.SCISSORS to "C",
        )
        val myShapeNames = mapOf(
            Shape.ROCK to "X",
            Shape.PAPER to "Y",
            Shape.SCISSORS to "Z",
        )

        val lineScore = Shape.entries.cartesianProduct(Shape.entries)
            .associate { (elfShape, myShape) ->
                "${elfShapeNames[elfShape]} ${myShapeNames[myShape]}" to myShape.points + myShape.outcome(elfShape).points
            }

        return input.lines()
            .sumOf { lineScore[it]!! }
    }

    enum class Shape(val points: Int) {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        fun outcome(other: Shape): Outcome {
            return when (other) {
                this -> Outcome.DRAW
                whatBeatsWhat[this] -> Outcome.WIN
                else -> Outcome.LOSS
            }
        }

        companion object {
            private val whatBeatsWhat = mapOf(
                ROCK to SCISSORS,
                PAPER to ROCK,
                SCISSORS to PAPER
            )
        }
    }

    enum class Outcome(val points: Int) {
        WIN(6),
        LOSS(0),
        DRAW(3)
    }
}