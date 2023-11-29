package cz.judas.jan.advent.year2022

import com.google.common.collect.HashMultiset
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.characters

object Day6 {
    fun part1(input: InputData): Int {
        val window = HashMultiset.create<Char>()
        val chars = input.asString().characters()
        for (i in chars.indices) {
            window.add(chars[i])
            if (i >= 4) {
                window.remove(chars[i - 4])
            }
            if (window.elementSet().size == 4) {
                return i + 1
            }
        }
        throw RuntimeException("End of input reached")
    }
}