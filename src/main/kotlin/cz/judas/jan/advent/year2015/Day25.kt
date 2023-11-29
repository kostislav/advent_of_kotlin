package cz.judas.jan.advent.year2015

import cz.judas.jan.advent.InputData

object Day25 {
    fun part1(input: InputData): Long {
        return codeFor(2981, 3075)
    }

    fun codeFor(row: Int, column: Int): Long {
        var exponent = indexFor(row, column) - 1
        var base = 252533L
        val module = 33554393
        var result = 1L
        while (exponent > 0) {
            if (exponent and 1 > 0) {
                result = (result * base) % module
            }
            base = (base * base) % module
            exponent = exponent shr 1
        }
        return (20151125 * result) % module
    }

    fun indexFor(row: Int, column: Int): Int {
        val layerNumber = row + column - 2
        val layerBase = layerNumber * (layerNumber + 1) / 2
        return layerBase + column
    }
}