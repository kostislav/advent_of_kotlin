package cz.judas.jan.advent.year2023

import com.google.common.collect.Range
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.StringTokenizer
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.asSequence
import cz.judas.jan.advent.length
import cz.judas.jan.advent.product
import cz.judas.jan.advent.times
import cz.judas.jan.advent.tokenize
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

object Day3 {
    @Answer("522726")
    fun part1(input: InputData): Int {
        val schematic = readInput(input)

        return schematic.values()
            .toSet()
            .mapNotNull { it.filterContentByType<BlockContent.Number>() }
            .filter { number -> number.adjacent().any { schematic.getOrNull(it)?.content is BlockContent.Symbol } }
            .sumOf { it.content.value }
    }

    @Answer("81721933")
    fun part2(input: InputData): Int {
        val schematic = readInput(input)

        return schematic.values()
            .mapNotNull { it.filterContentByType<BlockContent.Symbol>() }
            .sumOf { symbol ->
                val adjacentNumbers = symbol.adjacent()
                    .mapNotNull { schematic.getOrNull(it)?.filterContentByType<BlockContent.Number>() }
                    .toSet()

                if (adjacentNumbers.size == 2) {
                    adjacentNumbers.map { it.content.value }.product()
                } else {
                    0
                }
            }
    }

    private fun readInput(input: InputData): TwoDimensionalArray<SchemaNode> {
        val tokenizer = StringTokenizer.create(mapOf(
            "\\d+" to { BlockContent.Number(it.toInt()) },
            "\\." to { BlockContent.Nothing },
            "." to { BlockContent.Symbol(it[0]) }
        ))

        return TwoDimensionalArray.create(
            input.lines()
                .mapIndexed { row, line ->
                    line.tokenize(tokenizer)
                        .flatMap { token -> listOf(SchemaNode(row, token.position, token.content)) * token.position.length() }
                        .toList()
                }
        )
    }

    sealed interface BlockContent {
        data class Number(val value: Int) : BlockContent

        data class Symbol(val value: Char) : BlockContent

        data object Nothing : BlockContent
    }

    data class SchemaNode(val x: Int, val y: Range<Int>, val content: BlockContent) {
        inline fun <reified T: BlockContent> filterContentByType(): TypedSchemaNode<T>? {
            return if (content is T) {
                TypedSchemaNode(x, y, content)
            } else {
                null
            }
        }
    }

    data class TypedSchemaNode<T: BlockContent>(val x: Int, val y: Range<Int>, val content: T) {
        fun adjacent(): Sequence<Coordinate> {
            return sequence {
                for (row in IntRange(x - 1, x + 1)) {
                    yield(Coordinate(row, y.lowerEndpoint() - 1))
                    yield(Coordinate(row, y.upperEndpoint() + 1))
                }
                for (column in y.asSequence()) {
                    yield(Coordinate(x - 1, column))
                    yield(Coordinate(x + 1, column))
                }
            }
        }
    }
}