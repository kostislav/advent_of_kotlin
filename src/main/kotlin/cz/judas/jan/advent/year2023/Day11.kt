package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray
import kotlin.math.abs

object Day11 {
    @Answer("9591768")
    fun part1(input: InputData): Long {
        return expand(input, 2)
    }

    @Answer("746962097860")
    fun part2(input: InputData): Long {
        return expand(input, 1_000_000)
    }

    fun expand(input: InputData, factor: Int): Long {
        val toAdd = factor.toLong() - 1
        val image = TwoDimensionalArray.charsFromLines(input.lines())
        val emptyRows = image.rowIndices().filter { row ->
            image.columnIndices().all { column -> image[row, column] == '.' }
        }.toSortedSet()
        val emptyColumns = image.columnIndices().filter { column ->
            image.rowIndices().all { row -> image[row, column] == '.' }
        }.toSortedSet()
        val galaxies = image.entries()
            .filter { (_, value) -> value == '#' }
            .map { (position, _) -> position }
        val expandedGalaxies = galaxies.map { galaxy ->
            Pair(
                galaxy.first + emptyRows.headSet(galaxy.first).size * toAdd,
                galaxy.second + emptyColumns.headSet(galaxy.second).size * toAdd
            )
        }.toList()

        return expandedGalaxies
            .sumOf { galaxy ->
                expandedGalaxies.sumOf { abs(galaxy.first - it.first) + abs(galaxy.second - it.second) }
            } / 2
    }
}