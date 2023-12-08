package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.cycle
import cz.judas.jan.advent.leastCommonMultiple
import cz.judas.jan.advent.parserFor

object Day8 {
    @Answer("14681")
    fun part1(input: InputData): Int {
        val maps = parserFor<Maps>().parse(input.asString())

        return shortestPath("AAA", maps) { it == "ZZZ" }
    }

    @Answer("14321394058031")
    fun part2(input: InputData): Long {
        val maps = parserFor<Maps>().parse(input.asString())

        return maps.nodesByName.keys
            .filter { it.last() == 'A' }
            .map { node ->
                shortestPath(node, maps) { it.last() == 'Z' }
            }
            .map { it.toLong() }
            .leastCommonMultiple()
    }

    private fun shortestPath(startingNode: String, maps: Maps, target: (String) -> Boolean): Int {
        val iterator = maps.infiniteInstructions.iterator()
        return generateSequence(startingNode) {
            maps.nodesByName.getValue(it).next(iterator.next())
        }
            .takeWhile { !target(it) }
            .count()
    }

    @Pattern("(\\w+)\n\n(.+)")
    class Maps(
        instructions: List<Char>,
        nodes: @SplitOn("\n") List<Node>
    ) {
        val infiniteInstructions = instructions.cycle()
        val nodesByName = nodes.associateBy { it.thisNode }
    }

    @Pattern("(\\w+) = \\((\\w+), (\\w+)\\)")
    data class Node(
        val thisNode: String,
        val left: String,
        val right: String
    ) {
        fun next(instruction: Char): String {
            return if (instruction == 'L') left else right
        }
    }
}