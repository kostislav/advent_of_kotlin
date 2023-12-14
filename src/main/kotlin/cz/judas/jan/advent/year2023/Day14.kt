package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Mutable2dArray
import cz.judas.jan.advent.TwoDimensionalArray

object Day14 {
    fun part1(input: InputData): Int {
        val platform = TwoDimensionalArray.charsFromLines(input.lines())
        val debug = Mutable2dArray(platform.numRows, platform.numColumns, '.')
        return platform.columnIndices()
            .sumOf { column ->
                var total = 0
                var current = platform.numRows
                for (row in platform.rowIndices()) {
                    val field = platform[row, column]
                    if (field == '#') {
                        debug.set(row, column, '#')
                        current = platform.numRows - row - 1
                    } else if (field == 'O') {
                        debug.set(platform.numRows - current, column, 'O')
                        total += current
                        current -= 1
                    }
                }
                total
            }
    }

    fun part2(input: InputData): Int {
        var platform = TwoDimensionalArray.charsFromLines(input.lines())
        val history = mutableListOf<Int>()
        for (i in 0..1000000000L) {
            platform = spin(platform)
            val tempResult = load(platform)
            val candidates = history.mapIndexedNotNull { index, load -> if (load == tempResult) index else null }
            if (candidates.size >= 2) {
                for (prevIndex in candidates) {
                    val length = history.size - prevIndex
                    if (length > 1 && prevIndex - length >= 0 && history[prevIndex - length] == tempResult) {
                        if (history.subList(prevIndex - length, prevIndex) == history.subList(prevIndex, prevIndex + length)) {
                            return history[prevIndex - 1 + ((1000000000L - prevIndex) % length).toInt()]
                        }
                    }
                }
            }

            history += tempResult
        }
        return 0
    }

    private fun spin(platform: TwoDimensionalArray<Char>): TwoDimensionalArray<Char> {
        return platform.tilt()
            .rotateRight()
            .tilt()
            .rotateRight()
            .tilt()
            .rotateRight()
            .tilt()
            .rotateRight()
    }

    private fun TwoDimensionalArray<Char>.tilt(): TwoDimensionalArray<Char> {
        val tilted = Mutable2dArray(numRows, numColumns, '.')
        columnIndices()
            .forEach { column ->
                var current = numRows
                for (row in rowIndices()) {
                    val field = get(row, column)
                    if (field == '#') {
                        tilted.set(row, column, '#')
                        current = numRows - row - 1
                    } else if (field == 'O') {
                        tilted.set(numRows - current, column, 'O')
                        current -= 1
                    }
                }
            }
        return TwoDimensionalArray(numRows, numColumns) { row, column -> tilted.getSafe(row, column) }
    }

    private fun load(platform: TwoDimensionalArray<Char>): Int {
        return platform.columnIndices()
            .sumOf { column ->
                var load = 0
                for (row in platform.rowIndices()) {
                    val field = platform[row, column]
                    if (field == 'O') {
                        load += platform.numRows - row
                    }
                }
                load
            }
    }
}