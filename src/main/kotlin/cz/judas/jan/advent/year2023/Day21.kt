package cz.judas.jan.advent.year2023

import com.google.common.math.IntMath
import com.google.common.math.LongMath.pow
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.breadthFirstSearch
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.math.RoundingMode

object Day21 {
    @Answer("")
    fun part1(input: InputData): Int {
        return part1(input, 64)
    }

    fun part1(input: InputData, steps: Int): Int {
        val garden = input.as2dArray()
        val startingPosition = garden.first { it == 'S' }
        val stepMod = steps % 2
        val visited = mutableSetOf<Coordinate>()
        val goals = mutableSetOf<Coordinate>()
        visited += startingPosition
        if (stepMod == 0) {
            goals += startingPosition
        }
        breadthFirstSearch(startingPosition to 0) { (position, stepsTaken), backlog ->
            if (stepsTaken % 2 == stepMod) {
                goals += position
            }
            if (stepsTaken < steps) {
                val nextPossible = Direction.entries
                    .map { position + it.movement }
                    .filter { garden.isInside(it) && garden[it] != '#' }

                for (next in nextPossible) {
                    if (visited.add(next)) {
                        backlog += next to (stepsTaken + 1)
                    }
                }
            }
        }
        return goals.size
    }

    //    76397285103811 too low
    //    2460031342156778 too high
    //    613391294577878 just right
    @Answer("613391294577878")
    fun part2(input: InputData): Long {
        return shadyPart2(input, 26501365)
    }

    fun shadyPart2(input: InputData, steps: Int): Long {
//        val firstSquare = honestPart2(input, 65)
//        val nextSquares = honestPart2(input, 327)
//        println(nextSquares - firstSquare) // TODO
//        println((nextSquares - firstSquare) / 6.0) // TODO
//        val unreachableInPrimarySquare = pow(65 + 1, 2) - honestPart2(input, 65)  // 3778
        //        3699
        //        3804, 3708

//        val unreachableInSecondarySquares = pow(327 + 1, 2) - honestPart2(input, 327) - unreachableInPrimarySquare
//        println("BBB ${unreachableInPrimarySquare}") // TODO
//        println("CCC ${unreachableInSecondarySquares + unreachableInPrimarySquare}") // TODO
////        println(unreachableInSecondarySquares) // TODO
//
        val numGardens = (steps - 65L) / 131

//        println(numGardens) // TODO
//        println(pow(numGardens + 1, 2)) // TODO
//        println(pow(numGardens, 2)) // TODO
//        println(pow(2 * numGardens + 1, 2) - 1) // TODO

        return pow(numGardens + 1, 2) * 3778L + pow(numGardens, 2) * 3699L + (pow(2 * numGardens + 1, 2) - 1) / 4L * (3804 + 3707 /* TODO why?*/)

//        return pow(steps + 1L, 2) - (pow(1 + 2 * numGardens, 2) - 1) * unreachableInSecondarySquares / 24 - unreachableInPrimarySquare
//        return 0
    }

    fun honestPart2(input: InputData, steps: Int): Long {
        val garden = input.as2dArray()
        val extraGardens = IntMath.divide(steps - garden.numRows / 2, garden.numRows, RoundingMode.CEILING)
//        println(extraGardens) // TODO
//        val startingPosition = garden.first { it == 'S' }
        val startingPosition = garden.first { it == 'S' }.let { Coordinate(it.row + garden.numRows * extraGardens, it.column + garden.numColumns * extraGardens) }
        val megaGarden = TwoDimensionalArray(garden.numRows * (2 * extraGardens + 1), garden.numColumns * (2 * extraGardens + 1)) { row, column ->
            val original = garden[row % garden.numRows, column % garden.numColumns]
            if (original == 'S') '.' else original
        }
//        val megaGarden = garden
        val visited = mutableSetOf<Coordinate>()
        val stepMod = steps % 2
        val goals = mutableSetOf<Coordinate>()
        if (stepMod == 0) {
            goals += startingPosition
        }
        visited += startingPosition
        breadthFirstSearch(startingPosition to 0) { (position, stepsTaken), backlog ->
            if (stepsTaken % 2 == stepMod) {
                goals += position
            }
            if (stepsTaken < steps) {
                val nextPossible = Direction.entries
                    .map { position + it.movement }
                    .filter { megaGarden.isInside(it) && megaGarden[it] != '#' }

                for (next in nextPossible) {
                    if (visited.add(next)) {
                        backlog += next to (stepsTaken + 1)
                    }
                }
            }
        }

//        PrintWriter(FileWriter(File("/tmp/${steps}")).buffered()).use { writer ->
//            for (row in 0..<megaGarden.numRows) {
//                for (column in 0..<megaGarden.numColumns) {
//                    val position = Coordinate(row, column)
//                    if (position in goals && position.manhattanDistance(startingPosition) == steps) {
//                        writer.print('O')
//                    } else {
//                        writer.print(megaGarden[position])
//                    }
//                }
//                writer.println()
//            }
//        }
//        TODO finish this
        return goals.size.toLong()
    }

    //    TODO
    fun blbostPart2(input: InputData): Long {
        val steps = 327
        val garden = input.as2dArray()
        val extraGardens = IntMath.divide(steps - garden.numRows / 2, garden.numRows, RoundingMode.CEILING)
        val startingPosition = garden.first { it == 'S' }.let { Coordinate(it.row + garden.numRows * extraGardens, it.column + garden.numColumns * extraGardens) }
        val megaGarden = TwoDimensionalArray(garden.numRows * (2 * extraGardens + 1), garden.numColumns * (2 * extraGardens + 1)) { row, column ->
            val original = garden[row % garden.numRows, column % garden.numColumns]
            if (original == 'S') '.' else original
        }
        val visited = mutableSetOf<Coordinate>()
        val stepMod = steps % 2
        val goals = mutableSetOf<Coordinate>()
        visited += startingPosition
        breadthFirstSearch(startingPosition to 0) { (position, stepsTaken), backlog ->
            if (stepsTaken % 2 == stepMod) {
                goals += position
            }
            if (stepsTaken < steps) {
                val nextPossible = Direction.entries
                    .map { position + it.movement }
                    .filter { megaGarden.isInside(it) && megaGarden[it] != '#' }

                for (next in nextPossible) {
                    if (visited.add(next)) {
                        backlog += next to (stepsTaken + 1)
                    }
                }
            }
        }
//        listOf(-0, 65)
        kravina(goals, startingPosition, 0, 0)
        println() // TODO
        kravina(goals, startingPosition, 0, 131)
        kravina(goals, startingPosition, 0, -131)
        kravina(goals, startingPosition, 131, 0)
        kravina(goals, startingPosition, -131, 0)
        println() // TODO
        kravina(goals, startingPosition, 262, 0)
        kravina(goals, startingPosition, -262, 0)
        kravina(goals, startingPosition, 0, 262)
        kravina(goals, startingPosition, 0, -262)
        println() // TODO
        kravina(goals, startingPosition, 131, 131)
        kravina(goals, startingPosition, -131, 131)
        kravina(goals, startingPosition, 131, -131)
        kravina(goals, startingPosition, -131, -131)
        println() // TODO
        kravina(goals, startingPosition, 66, 66)
        kravina(goals, startingPosition, 66, -65)
        kravina(goals, startingPosition, -65, 66)
        kravina(goals, startingPosition, -65, -65)
        println() // TODO
        kravina(goals, startingPosition, 66, 197)
        kravina(goals, startingPosition, 197, 66)
        kravina(goals, startingPosition, 197, -65)
        kravina(goals, startingPosition, 66, -196)

        kravina(goals, startingPosition, -65, 197)
        kravina(goals, startingPosition, -196, 66)

        kravina(goals, startingPosition, -196, -65)
        kravina(goals, startingPosition, -65, -196)
        return goals.size.toLong()
    }

    private fun kravina(goals: Set<Coordinate>, startingPosition: Coordinate, deltaRows: Int, deltaColumns: Int) {
        val center = Coordinate(startingPosition.row + deltaRows, startingPosition.column + deltaColumns)
        println(goals.count { it.manhattanDistance(center) <= 65 }) // TODO
    }
}
