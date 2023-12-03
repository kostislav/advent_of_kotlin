package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.StringTokenizer
import cz.judas.jan.advent.UndirectedGraph
import cz.judas.jan.advent.product
import cz.judas.jan.advent.tokenize
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
                        neighborNumbers.map { it.value }.product()
                    } else {
                        0
                    }
                } else {
                    0
                }
            }
    }

    private fun buildGraph(input: InputData): UndirectedGraph<SchemaNode> {
        val tokenizer = StringTokenizer.create(mapOf(
            "\\d+" to { BlockContent.Number(it.toInt()) },
            "\\." to { BlockContent.Nothing },
            "." to { BlockContent.Symbol(it[0]) }
        ))

        val graphBuilder = UndirectedGraph.builder<SchemaNode>()
        var previousLine: NavigableMap<Int, SchemaNode> = TreeMap()
        input.lines().forEachIndexed { i, line ->
            val thisLine: NavigableMap<Int, SchemaNode> = TreeMap()
            val tokens = line.tokenize(tokenizer)
            for (token in tokens) {
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

    sealed interface BlockContent {
        data class Number(val value: Int) : BlockContent

        data class Symbol(val value: Char) : BlockContent

        data object Nothing : BlockContent
    }

    data class SchemaNode(val x: Int, val y: IntRange, val content: BlockContent)
}