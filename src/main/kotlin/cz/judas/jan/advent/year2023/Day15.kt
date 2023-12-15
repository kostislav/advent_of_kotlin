package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.mutableMapWithDefault
import cz.judas.jan.advent.splitOnOnly

object Day15 {
    @Answer("515210")
    fun part1(input: InputData): Int {
        return input.asString()
            .filter { it != '\n' }
            .split(",")
            .sumOf(::hash)
    }

    @Answer("246762")
    fun part2(input: InputData): Long {
        val boxes = mutableMapWithDefault<Int, MutableMap<String, Int>> { LinkedHashMap() }
        input.asString()
            .filter { it != '\n' }
            .split(",").forEach { operation ->
                if (operation.contains('=')) {
                    val (label, lens) = operation.splitOnOnly("=")
                    boxes.getOrCreate(hash(label))[label] = lens.toInt()
                } else {
                    val label = operation.substringBefore('-')
                    boxes.getOrCreate(hash(label)) -= label
                }
            }
        return boxes.map { (box, lenses) ->
            (box + 1L) * lenses.entries.mapIndexed { i, (_, lens) -> (i + 1) * lens }.sum()
        }
            .sum()
    }

    private fun hash(input: String): Int {
        return input.fold(0) { hash, current -> (hash + current.code) * 17 % 256 }
    }
}