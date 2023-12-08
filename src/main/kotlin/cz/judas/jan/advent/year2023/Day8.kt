package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.cycle
import cz.judas.jan.advent.parserFor
import org.apache.commons.math3.util.ArithmeticUtils

object Day8 {

    @Answer("14681")
    fun part1(input: InputData): Int {
        val maps = parserFor<Maps>().parse(input.asString())
        val nodes = maps.nodes.associateBy { it.thisNode }
        val instructions = maps.instructions.cycle().iterator()

        return generateSequence("AAA") { nodes.getValue(it).next(instructions.next()) }
            .takeWhile { it != "ZZZ" }
            .count()
    }

    @Answer("14321394058031")
    fun part2(input: InputData): Long {
        val maps = parserFor<Maps>().parse(input.asString())
        val nodes = maps.nodes.associateBy { it.thisNode }
        val instructions = maps.instructions.cycle()

        val shortestPaths = nodes.keys
            .filter { it.last() == 'A' }
            .toList()
            .map { shortestPath(it, nodes, instructions) }

        return shortestPaths.drop(2)
            .fold(ArithmeticUtils.lcm(shortestPaths[0], shortestPaths[1]).toLong()) { soFar, current ->
                ArithmeticUtils.lcm(soFar, current.toLong())
            }
    }

    private fun shortestPath(startingNode: String, nodes: Map<String, Node>, instructions: Sequence<Char>): Int {
        val iterator = instructions.iterator()
        return generateSequence(startingNode) { nodes.getValue(it).next(iterator.next()) }
            .takeWhile { it.last() != 'Z' }
            .count()
    }

    @Pattern("(\\w+)\n\n(.+)")
    data class Maps(
        val instructions: List<Char>,
        val nodes: @SplitOn("\n") List<Node>
    )

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