package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray
import cz.judas.jan.advent.breadthFirstSearch
import cz.judas.jan.advent.getOnlyElement
import cz.judas.jan.advent.mutableMapWithDefault
import cz.judas.jan.advent.recursive

object Day23 {
    @Answer("2370")
    fun part1(input: InputData): Int {
        val map = input.as2dArray()
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

    @Answer("6546")
    fun part2(input: InputData): Int {
        val map = input.as2dArray()
        val start = map.first { it == '.' }
        val end = map.last { it == '.' }
        val edgeList = buildGraph(map, start, end, null)
        val edges = mutableMapWithDefault<Coordinate, MutableMap<Coordinate, Int>> { mutableMapOf() }
        for (edge in edgeList) {
            edges.getOrCreate(edge.end)[edge.start] = edge.weight
            edges.getOrCreate(edge.start)[edge.end] = edge.weight
        }
        val nodeMasks = edges.keys.mapIndexed { index, node -> node to (1L shl index) }.toMap()

        return recursive(start, 0L, cached = false) { current, visited, recursion ->
            if (current == end) {
                0
            } else {
                var longestPath: Int? = null
                for ((neighbor, weight) in edges.getValue(current)) {
                    val neighborMask = nodeMasks.getValue(neighbor)
                    if (neighborMask and visited == 0L) {
                        val longestPathFromNeighbor = recursion(neighbor, visited + neighborMask)
                        if (longestPathFromNeighbor !== null) {
                            val longestPathThroughNeighbor = weight + longestPathFromNeighbor
                            if (longestPath === null || longestPath < longestPathThroughNeighbor) {
                                longestPath = longestPathThroughNeighbor
                            }
                        }
                    }
                }
                longestPath
            }
        }!!
    }

    private fun buildGraph(
        map: TwoDimensionalArray<Char>,
        start: Coordinate,
        end: Coordinate,
        slopes: Map<Char, Direction>?
    ): List<Edge> {
        val edges = mutableListOf<Edge>()
        val intersections = mutableSetOf<Coordinate>()
        val visited = mutableSetOf(start)
        breadthFirstSearch(start to Direction.DOWN) { (currentPathStart, startingDirection), backlog ->
            var length = 1
            var current = currentPathStart + startingDirection
            if (current !in visited) {
                while (current != end && current !in intersections) {
                    val possibleDirections = Direction.entries.filter { (map.getOrDefault(current + it, '#')) != '#' }
                    if (possibleDirections.size > 2) {
                        intersections += current
                        for (nextDirection in possibleDirections) {
                            val nextPosition = current + nextDirection
                            if (nextPosition !in visited && (slopes === null || nextDirection.inverse() != slopes[map[nextPosition]])) {
                                backlog += current to nextDirection
                            }
                        }
                    } else {
                        visited += current
                        val remainingDirections = possibleDirections.filter { current + it !in visited && current + it != currentPathStart }
                        val nextDirection = remainingDirections.getOnlyElement()
                        val nextPosition = current + nextDirection
                        if (slopes === null || nextDirection.inverse() != slopes[map[nextPosition]]) {
                            current += nextDirection
                            length += 1
                        }
                    }
                }
                edges += Edge(currentPathStart, current, length)
            }
        }
        return edges
    }

    data class Edge(val start: Coordinate, val end: Coordinate, val weight: Int)
}
