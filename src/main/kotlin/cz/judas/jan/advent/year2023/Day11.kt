package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.Vector2d
import cz.judas.jan.advent.cartesianProduct

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
        val toAdd = factor - 1
        val image = TwoDimensionalArray.charsFromLines(input.lines())
        val emptyRows = image.rows()
            .mapIndexedNotNull { i, row -> if (row.all { it == '.' }) i else null }
            .toSortedSet()
        val emptyColumns = image.columns()
            .mapIndexedNotNull { j, column -> if (column.all { it == '.' }) j else null }
            .toSortedSet()
        val galaxies = image.entries()
            .filter { (_, value) -> value == '#' }
            .map { (position, _) ->
                position + Vector2d(
                    emptyRows.headSet(position.row).size * toAdd,
                    emptyColumns.headSet(position.column).size * toAdd
                )
            }.toList()

        return galaxies.cartesianProduct(galaxies)
            .sumOf { (first, second) -> first.manhattanDistance(second).toLong() } / 2
    }
}