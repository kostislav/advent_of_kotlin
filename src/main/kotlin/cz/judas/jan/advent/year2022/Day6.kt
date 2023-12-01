package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.characters
import cz.judas.jan.advent.mutableMultisetOf

object Day6 {
    fun part1(input: InputData): Int {
        val window = mutableMultisetOf<Char>()
        val chars = input.asString().characters()
        for (i in chars.indices) {
            window += chars[i]
            if (i >= 4) {
                window -= chars[i - 4]
            }
            if (window.elementSet().size == 4) {
                return i + 1
            }
        }
        throw RuntimeException("End of input reached")
    }
}