package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.splitOnOnly

object Day15 {
    fun part1(input: InputData): Int {
        return input.asString()
            .filter { it != '\n' }
            .split(",")
            .sumOf(::hash)
    }

    fun part2(input: InputData): Long {
        val boxes = mutableMapOf<Int, MutableMap<String, Int>>()
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
        return input.fold(0) { hash, current -> (hash + current.code) * 17 % 256 } as Int
    }

    fun MutableMap<Int, MutableMap<String, Int>>.getOrCreate(key: Int): MutableMap<String, Int> {
        val value = get(key)
        return if (value === null) {
            val newValue = LinkedHashMap<String, Int>()
            put(key, newValue)
            newValue
        } else {
            value
        }
    }
}