package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.biMapOf
import cz.judas.jan.advent.cartesianProduct

object Day2 {
    private val elfShapeNames = mapOf(
        Shape.ROCK to "A",
        Shape.PAPER to "B",
        Shape.SCISSORS to "C",
    )

    fun part1(input: InputData): Int {
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
            .sumOf(lineScore::getValue)
    }

    fun part2(input: InputData): Int {
        val outcomeNames = mapOf(
            Outcome.LOSS to "X",
            Outcome.DRAW to "Y",
            Outcome.WIN to "Z",
        )

        val lineScore = Shape.entries.cartesianProduct(Outcome.entries)
            .associate { (elfShape, outcome) ->
                "${elfShapeNames[elfShape]} ${outcomeNames[outcome]}" to elfShape.shapeFor(outcome).points + outcome.points
            }

        return input.lines()
            .sumOf(lineScore::getValue)
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

        fun shapeFor(outcome: Outcome): Shape {
            return when (outcome) {
                Outcome.WIN -> whatBeatsWhat.inverse().getValue(this)
                Outcome.DRAW -> this
                Outcome.LOSS -> whatBeatsWhat.getValue(this)
            }
        }

        companion object {
            private val whatBeatsWhat = biMapOf(
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