package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.breadthFirstSearch
import cz.judas.jan.advent.mutableMapWithDefault

object Day23 {
    @Answer("")
    fun part1(input: InputData): Int {
        val map = TwoDimensionalArray.charsFromLines(input.lines())
        val start = map.first { it == '.' }
        val end = map.last { it == '.' }
        val slopes = mapOf(
            '^' to Direction.UP,
            'v' to Direction.DOWN,
            '<' to Direction.LEFT,
            '>' to Direction.RIGHT,
        )
        val edgeList = buildGraph(map, start, end, slopes)
        val invertedEdges = mutableMapWithDefault<Coordinate, MutableMap<Coordinate, Int>> { mutableMapOf() }
        val edges = mutableMapWithDefault<Coordinate, MutableSet<Coordinate>> { mutableSetOf() }
        for (edge in edgeList) {
            invertedEdges.getOrCreate(edge.end)[edge.start] = edge.weight
            edges.getOrCreate(edge.start) += edge.end
        }
        val linearized = mutableSetOf<Coordinate>()
        breadthFirstSearch(
            start
        ) { node, backlog ->
            linearized += node
            for (neighbor in edges[node] ?: emptySet()) {
                if (invertedEdges.getValue(neighbor).keys.all { it in linearized }) {
                    backlog += neighbor
                }
            }
        }
        val longestPaths = mutableMapOf(
            start to 0
        )
        for (node in linearized) {
            if (node != start) {
                longestPaths[node] = invertedEdges.getValue(node).entries.maxOfOrNull { (neighbor, length) -> longestPaths.getValue(neighbor) + length }!!
            }
        }
        return longestPaths.getValue(end)
    }

    @Answer("")
    fun part2(input: InputData): Int {
        return 0
    }

    private fun buildGraph(
        map: TwoDimensionalArray<Char>,
        start: Coordinate,
        end: Coordinate,
        slopes: Map<Char, Direction>
    ): MutableList<Edge> {
        val edges = mutableListOf<Edge>()
        val visited = mutableSetOf<Coordinate>()
        val backlog = mutableListOf(Edge(start, start + Direction.DOWN, 1))
        visited += start
        while (backlog.isNotEmpty()) {
            val partialEdge = backlog.removeFirst()
            val (previous, current, weight) = partialEdge
            if (current == end) {
                edges += partialEdge
            } else {
                val possibleDirections = Direction.entries.filter { map[current + it] != '#' }
                if (possibleDirections.size > 2) {
                    edges += partialEdge
                } else {
                    visited += current
                }

                val (nextStart, nextWeight) = if (possibleDirections.size > 2) current to 1 else previous to weight + 1
                for (direction in possibleDirections) {
                    val nextPosition = current + direction
                    if (nextPosition !in visited) {
                        val nextSymbol = map[nextPosition]
                        if (nextSymbol == '.') {
                            backlog += Edge(nextStart, nextPosition, nextWeight)
                        } else if (nextSymbol != '#') {
                            val slope = slopes.getValue(nextSymbol)
                            if (slope != direction.inverse()) {
                                backlog += Edge(nextStart, nextPosition + slope, nextWeight + 1)
                            }
                        }
                    }
                }
            }
        }
        return edges
    }

    data class Edge(val start: Coordinate, val end: Coordinate, val weight: Int)
}
