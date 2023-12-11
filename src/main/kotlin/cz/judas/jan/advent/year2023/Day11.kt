package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray
import kotlin.math.abs

object Day11 {
    fun part1(input: InputData): Int {
        val image = TwoDimensionalArray.charsFromLines(input.lines())
        val emptyRows = (0..<image.numRows).filter { row ->
            (0..<image.numColumns).all { column -> image[row, column] == '.' }
        }.toSortedSet()
        val emptyColumns = (0..<image.numColumns).filter { column ->
            (0..<image.numRows).all { row -> image[row, column] == '.' }
        }.toSortedSet()
        val galaxies = (0..<image.numRows).flatMap { row ->
            (0..<image.numColumns).mapNotNull { column -> if (image[row, column] == '#') Pair(row, column) else null  }
        }
        val expandedGalaxies = galaxies.map { galaxy ->
            Pair(
                galaxy.first + emptyRows.headSet(galaxy.first).size,
                galaxy.second + emptyColumns.headSet(galaxy.second).size
            )
        }

        return expandedGalaxies
            .sumOf { galaxy ->
                expandedGalaxies.filter { it != galaxy }.sumOf { abs(galaxy.first - it.first) + abs(galaxy.second - it.second) }
            } / 2
    }

    fun part2(input: InputData): Long {
        val image = TwoDimensionalArray.charsFromLines(input.lines())
        val emptyRows = (0..<image.numRows).filter { row ->
            (0..<image.numColumns).all { column -> image[row, column] == '.' }
        }.toSortedSet()
        val emptyColumns = (0..<image.numColumns).filter { column ->
            (0..<image.numRows).all { row -> image[row, column] == '.' }
        }.toSortedSet()
        val galaxies = (0..<image.numRows).flatMap { row ->
            (0..<image.numColumns).mapNotNull { column -> if (image[row, column] == '#') Pair(row, column) else null  }
        }
        val expandedGalaxies = galaxies.map { galaxy ->
            Pair(
                galaxy.first + emptyRows.headSet(galaxy.first).size * 999999L,
                galaxy.second + emptyColumns.headSet(galaxy.second).size * 999999L
            )
        }

        return expandedGalaxies
            .sumOf { galaxy ->
                expandedGalaxies.filter { it != galaxy }.sumOf { abs(galaxy.first - it.first) + abs(galaxy.second - it.second) }
            } / 2
    }
}