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
            .sumOf { score(TwoDimensionalArray.charsFromLines(it), 0) }
    }

    @Answer("32192")
    fun part2(input: InputData): Int {
        return input.lines()
            .splitOn { it.isEmpty() }
            .sumOf { score(TwoDimensionalArray.charsFromLines(it), 1) }
    }

    private fun score(pattern: TwoDimensionalArray<Char>, diff: Int): Int {
        return horizontalScore(pattern, diff) * 100 + horizontalScore(pattern.transpose(), diff)
    }

    private fun horizontalScore(pattern: TwoDimensionalArray<Char>, diff: Int): Int {
        return pattern.rowIndices().filter { rowIndex ->
            val remainingRowCount = pattern.numRows - 1 - rowIndex
            val mirroredRowCount = min(rowIndex, remainingRowCount - 1)
            remainingRowCount > 0 && (0..mirroredRowCount).sumOf { rowMirrorDiff(pattern, rowIndex, it) } == diff
        }.sumOf { (it + 1) }
    }

    private fun rowMirrorDiff(pattern: TwoDimensionalArray<Char>, rowIndex: Int, offset: Int): Int {
        return pattern.row(rowIndex + 1 + offset)
            .zip(pattern.row(rowIndex - offset))
            .count { it.first != it.second }
    }
}