package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.Direction
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.TwoDimensionalArray

object Day16 {
    fun part1(input: InputData): Int {
        val contraption = TwoDimensionalArray.charsFromLines(input.lines())
        return numEnergized(contraption, Coordinate(0, 0), Direction.RIGHT)
    }

    fun part2(input: InputData): Int {
        val contraption = TwoDimensionalArray.charsFromLines(input.lines())
        return listOf(
            contraption.rowIndices().map { row -> Coordinate(row, 0) to Direction.RIGHT },
            contraption.rowIndices().map { row -> Coordinate(row, contraption.numColumns - 1) to Direction.LEFT },
            contraption.columnIndices().map { column -> Coordinate(0, column) to Direction.DOWN },
            contraption.columnIndices().map { column -> Coordinate(contraption.numRows - 1, column) to Direction.UP },
        ).flatten()
            .maxOf { numEnergized(contraption, it.first, it.second) }
    }

    private fun next(current: Coordinate, direction: Direction): Pair<Coordinate, Direction> {
        return current + direction.movement to direction
    }

    private fun numEnergized(
        contraption: TwoDimensionalArray<Char>,
        startingCoordinate: Coordinate,
        startingDirection: Direction
    ): Int {
        val visited = mutableSetOf<Pair<Coordinate, Direction>>()
        val queue = ArrayDeque<Pair<Coordinate, Direction>>()
        queue += startingCoordinate to startingDirection
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val (position, direction) = current
            val tile = contraption.getOrNull(position)
            if (current !in visited && tile !== null) {
                visited += current
                when (tile) {
                    '.' -> queue += next(position, direction)
                    '/' -> queue += when (direction) {
                        Direction.UP -> next(position, Direction.RIGHT)
                        Direction.DOWN -> next(position, Direction.LEFT)
                        Direction.LEFT -> next(position, Direction.DOWN)
                        Direction.RIGHT -> next(position, Direction.UP)
                    }

                    '\\' -> queue += when (direction) {
                        Direction.UP -> next(position, Direction.LEFT)
                        Direction.DOWN -> next(position, Direction.RIGHT)
                        Direction.LEFT -> next(position, Direction.UP)
                        Direction.RIGHT -> next(position, Direction.DOWN)
                    }

                    '-' -> {
                        when (direction) {
                            Direction.UP, Direction.DOWN -> {
                                queue += next(position, Direction.LEFT)
                                queue += next(position, Direction.RIGHT)
                            }

                            Direction.LEFT, Direction.RIGHT -> queue += next(position, direction)
                        }
                    }

                    '|' -> {
                        when (direction) {
                            Direction.UP, Direction.DOWN -> queue += next(position, direction)
                            Direction.LEFT, Direction.RIGHT -> {
                                queue += next(position, Direction.UP)
                                queue += next(position, Direction.DOWN)
                            }
                        }
                    }
                }
            }
        }
        return visited.map { it.first }.toSet().size
    }
}