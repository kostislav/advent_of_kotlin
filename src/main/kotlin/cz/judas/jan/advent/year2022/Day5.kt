package cz.judas.jan.advent.year2022

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.characters
import cz.judas.jan.advent.splitOnOnly

object Day5 {
    fun part1(input: InputData): String {
        val (crates, instructions) = input.lines().splitOnOnly { it.isEmpty() }
        val cratesArray = TwoDimensionalArray.charsFromLines(crates).rotateRight()
        val stacks = (1..cratesArray.numRows step 4)
            .associate {
                val line = cratesArray.row(it).joinToString("")
                val name = line.substring(0, 1)
                val stack = ArrayDeque(line.substring(1).trimEnd().characters())
                name to stack
            }

        for (line in instructions) {
            val parts = line.split(' ')
            val howMany = parts[1].toInt()
            val fromStack = stacks.getValue(parts[3])
            val toStack = stacks.getValue(parts[5])

            for (i in 1..howMany) {
                toStack.addLast(fromStack.removeLast())
            }
        }

        return stacks.map { it.value.last() }.joinToString("")
    }
}