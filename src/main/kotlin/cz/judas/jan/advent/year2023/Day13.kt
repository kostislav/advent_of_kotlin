package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.splitOn
import kotlin.math.min

object Day13 {
    @Answer("29165")
    fun part1(input: InputData): Int {
        return input.lines()
            .splitOn { it.isEmpty() }
            .sumOf { patternChars ->
                val pattern = TwoDimensionalArray.charsFromLines(patternChars)
                val (horizontalLines, verticalLines) = reflectionLines(pattern)
                horizontalLines.sumOf { (it + 1) * 100 } + verticalLines.sumOf { it + 1 }
            }
    }

    @Answer("32192")
    fun part2(input: InputData): Int {
        return input.lines()
            .splitOn { it.isEmpty() }
            .sumOf { patternChars ->
                val pattern = TwoDimensionalArray.charsFromLines(patternChars)
                val (horizontalLines, verticalLines) = reflectionLines(pattern)
                pattern.rowIndices().sumOf { modifiedRow ->
                    pattern.columnIndices().sumOf { modifiedColumn ->
                        val modifiedPattern = pattern.mapIndexed { row, column, value ->
                            if (row == modifiedRow && column == modifiedColumn) {
                                if (value == '.') '#' else '.'
                            } else {
                                value
                            }
                        }
                        val (modifiedHorizontalLines, modifiedVerticalLines) = reflectionLines(modifiedPattern)
                        val differentHorizontalLines = modifiedHorizontalLines - horizontalLines
                        val differentVerticalLines = modifiedVerticalLines - verticalLines
                        differentHorizontalLines.sumOf { (it + 1) * 100 } + differentVerticalLines.sumOf { it + 1 }
                    }
                }
            } / 2
    }

    private fun reflectionLines(pattern: TwoDimensionalArray<Char>): Pair<Set<Int>, Set<Int>> {
        val horizontalLines = pattern.rowIndices().filter { rowIndex ->
                rowIndex + 1 < pattern.numRows
                && (0..(min(rowIndex, pattern.numRows - rowIndex - 2)))
                    .all { offset -> pattern.row(rowIndex + 1 + offset).toList() == pattern.row(rowIndex - offset).toList() }
        }.toSet()

        val verticalLines = pattern.columnIndices().filter { columnIndex ->
                columnIndex + 1 < pattern.numColumns
                && (0..(min(columnIndex, pattern.numColumns - columnIndex - 2)))
                    .all { offset -> pattern.column(columnIndex + 1 + offset).toList() == pattern.column(columnIndex - offset).toList() }
        }.toSet()

        return Pair(horizontalLines, verticalLines)
    }
}