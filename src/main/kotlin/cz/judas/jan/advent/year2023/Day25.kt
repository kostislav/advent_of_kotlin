package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.breadthFirstSearch
import cz.judas.jan.advent.histogram
import cz.judas.jan.advent.mutableMapWithDefault
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.shortestPath
import kotlin.random.Random

object Day25 {
    @Answer("")
    fun part1(input: InputData): Int {
        val parser = parserFor<InputLine>()
        val edges = mutableMapWithDefault<String, MutableMap<String, Int>> { mutableMapOf() }
        input.lines()
            .map(parser::parse)
            .flatMap { line -> line.neighbors.map { line.name to it } }
            .forEach {
                edges.getOrCreate(it.first)[it.second] = 1
                edges.getOrCreate(it.second)[it.first] = 1
            }
        val nodes = edges.keys.toList()

//        val minDistances = mutableMapWithDefault<String, MutableMap<String, Int>> { mutableMapOf() }
//        val previousNodes = mutableMapWithDefault<String, MutableMap<String, String>> { mutableMapOf() }
//        edges.forEach { (node, nodeEdges) ->
//            nodeEdges.forEach { (otherNode, weight) ->
//                minDistances.getOrCreate(node)[otherNode] = weight
//                previousNodes.getOrCreate(node)[otherNode] = node
//            }
//        }
//        nodes.forEach { node ->
//            minDistances.getOrCreate(node)[node] = 0
//            previousNodes.getOrCreate(node)[node] = node
//        }
//        nodes.forEach { k ->
//            nodes.forEach { i ->
//                nodes.forEach { j ->
//                    val ik = minDistances.getValue(i)[k]
//                    val kj = minDistances.getValue(k)[j]
//                    val ij = minDistances.getValue(i)[j]
//                    if (ik !== null && kj !== null && (ij === null || ij > ik + kj)) {
//                        minDistances.getValue(i)[j] = ik + kj
//                        previousNodes.getValue(i)[j] = previousNodes.getValue(k).getValue(j)
//                    }
//                }
//            }
//        }

        val random = Random.Default
        val mostFrequentedEdges = (0..1000).asSequence()
            .map { random.nextInt(nodes.size) to random.nextInt(nodes.size)  }
            .flatMap { (i, j) ->
                shortestPath(nodes[i], nodes[j], edges::getValue)
                    .windowed(2)
                    .map { it[0] to it[1] }
            }
            .asIterable()
            .histogram()
            .entries
            .filter { it.key.first < it.key.second }
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key }
            .toSet()

        mostFrequentedEdges.forEach {
            edges.getValue(it.first) -= it.second
            edges.getValue(it.second) -= it.first
        }

        val visited = mutableSetOf<String>()
        breadthFirstSearch(nodes[0]) { node, backlog ->
            for (neighbor in edges.getValue(node).keys) {
                if (neighbor !in visited) {
                    visited += neighbor
                    backlog += neighbor
                }
            }
        }

        return visited.size * (nodes.size - visited.size)
    }

    @Answer("")
    fun part2(input: InputData): String {
        return "woooooo"
    }

    @Pattern("([a-z]+): (.+)")
    data class InputLine(
        val name: String,
        val neighbors: @SplitOn(" ") List<String>
    )
}
