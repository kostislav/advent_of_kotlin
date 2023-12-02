package cz.judas.jan.advent.year2015

import cz.judas.jan.advent.InputData

object Day1 {
    fun part1(input: InputData): Int {
        return input.asString()
            .fold(0) { current, symbol -> current + direction(symbol) }
    }

    fun part2(input: InputData): Int {
        return input.asString()
            .scan(IndexedFloor(0, 0)) { current, symbol -> current.move(direction(symbol))}
            .find { it.floor == -1 }!!
            .index
    }

    private fun direction(symbol: Char): Int {
        return when(symbol) {
            '(' -> 1
            ')' -> -1
            else -> throw RuntimeException("Unexpected symbol ${symbol}")
        }
    }

    private data class IndexedFloor(val index: Int, val floor: Int) {
        fun move(diff: Int) = IndexedFloor(index + 1, floor + diff)
    }
}