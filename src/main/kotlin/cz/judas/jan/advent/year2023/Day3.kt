package cz.judas.jan.advent.year2023

import com.google.common.collect.HashMultimap
import cz.judas.jan.advent.InputData
import java.util.*

object Day3 {
    fun part1(input: InputData): Int {
        val graph = buildGraph(input)

        return graph.nodes
            .sumOf { node ->
                if (
                    node.content is BlockContent.Number
                    && graph.neighborsOf(node).any { neighbor -> neighbor.content is BlockContent.Symbol }
                ) {
                    node.content.value
                } else {
                    0
                }
            }
    }

    fun part2(input: InputData): Int {
        val graph = buildGraph(input)

        return graph.nodes
            .sumOf { node ->
                if (node.content == BlockContent.Symbol('*')) {
                    val neighborNumbers = graph.neighborsOf(node)
                        .map { it.content }
                        .filterIsInstance(BlockContent.Number::class.java)

                    if (neighborNumbers.size == 2) {
                        neighborNumbers[0].value * neighborNumbers[1].value
                    } else {
                        0
                    }
                } else {
                    0
                }
            }
    }

    private fun buildGraph(input: InputData): UndirectedGraph<SchemaNode> {
        val graphBuilder = UndirectedGraph.builder<SchemaNode>()
        var previousLine: NavigableMap<Int, SchemaNode> = TreeMap()
        input.lines().forEachIndexed { i, line ->
            val thisLine: NavigableMap<Int, SchemaNode> = TreeMap()
            for (token in tokenize(line)) {
                val node = SchemaNode(i, token.position, token.content)
                for (neighbor in previousLine.subMap(
                    previousLine.floorKey(token.position.first - 1) ?: 0,
                    true,
                    previousLine.ceilingKey(token.position.last + 1) ?: line.length,
                    true
                ).values) {
                    graphBuilder.addEdge(neighbor, node)
                }
                thisLine.lastEntry()?.let { graphBuilder.addEdge(node, it.value) }
                thisLine.put(token.position.first, node)
            }
            previousLine = thisLine
        }
        return graphBuilder.build()
    }

    private fun tokenize(line: String): List<Token<BlockContent>> {
        val result = mutableListOf<Token<BlockContent>>()
        var j = 0
        while (j < line.length) {
            if (line[j] == '.') {
                result += Token(j..j, BlockContent.Nothing)
            } else if (line[j] in '0'..'9') {
                val start = j
                var number = line[j].code - '0'.code
                while (j + 1 < line.length && line[j + 1] in '0'..'9') {
                    number = number * 10 + line[j + 1].code - '0'.code
                    j += 1
                }
                result += Token(start..j, BlockContent.Number(number))
            } else {
                result += Token(j..j, BlockContent.Symbol(line[j]))
            }
            j += 1
        }
        return result
    }

    sealed interface BlockContent {
        data class Number(val value: Int) : BlockContent

        data class Symbol(val value: Char) : BlockContent

        data object Nothing : BlockContent
    }

    data class Token<T>(val position: IntRange, val content: T)

    data class SchemaNode(val x: Int, val y: IntRange, val content: BlockContent)

    class UndirectedGraph<T>(private val edges: HashMultimap<T, T>) {
        val nodes: Set<T> get() = edges.keySet()

        fun neighborsOf(node: T): Set<T> {
            return edges.get(node) ?: emptySet()
        }

        class Builder<T> {
            private val edges: HashMultimap<T, T> = HashMultimap.create()

            fun addEdge(node1: T, node2: T) {
                edges.put(node1, node2)
                edges.put(node2, node1)
            }

            fun build(): UndirectedGraph<T> {
                return UndirectedGraph(edges)
            }
        }

        companion object {
            fun <T> builder(): Builder<T> = Builder()
        }
    }
}