package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Coordinate
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Mutable2dArray
import cz.judas.jan.advent.TwoDimensionalArray

object Day10 {
    @Answer("6842")
    fun part1(input: InputData): Int {
        val (diagram, startingPosition) = loadDiagram(input)

        val steps = generateSequence(Step(startingPosition, diagram[startingPosition].first())) { step ->
            val nextPosition = step.nextPosition()
            val nextDirection = diagram[nextPosition].first { it != step.direction.inverse() }
            Step(nextPosition, nextDirection)
        }
        val length = steps.drop(1).takeWhile { it.position != startingPosition }.count()
        return (length + 1) / 2
    }

    @Answer("393")
    fun part2(input: InputData): Int {
        val (diagram, startingPosition) = loadDiagram(input)

        val startingDirections = diagram[startingPosition]
        val analyzed = Mutable2dArray<FieldType?>(diagram.numRows, diagram.numColumns, null)
        var currentDirection = startingDirections.first()
        var currentPosition = startingPosition + currentDirection.movement
        var rightBias = 0
        while (currentPosition != startingPosition) {
            val previousDirection = currentDirection
            currentDirection = diagram[currentPosition]
                .first { it != currentDirection.inverse() }

            when (previousDirection) {
                Direction.UP ->
                    when (currentDirection) {
                        Direction.UP -> {
                            updateField(analyzed, currentPosition, 0, -1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 0, 1, FieldType.RIGH)
                        }

                        Direction.RIGHT -> {
                            updateField(analyzed, currentPosition, 0, -1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, -1, -1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, -1, 0, FieldType.LEFT)
                            updateField(analyzed, currentPosition, +1, 1, FieldType.RIGH)
                            rightBias += 1
                        }

                        Direction.LEFT -> {
                            updateField(analyzed, currentPosition, 0, 1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, 1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, -1, FieldType.LEFT)
                            rightBias -= 1
                        }

                        else -> {}
                    }

                Direction.DOWN -> {
                    when (currentDirection) {
                        Direction.DOWN -> {
                            updateField(analyzed, currentPosition, 0, 1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 0, -1, FieldType.RIGH)
                        }

                        Direction.RIGHT -> {
                            updateField(analyzed, currentPosition, 0, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, 1, FieldType.LEFT)
                            rightBias -= 1
                        }

                        Direction.LEFT -> {
                            updateField(analyzed, currentPosition, -1, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 0, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 1, 1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 0, 1, FieldType.LEFT)
                            rightBias += 1
                        }

                        else -> {}
                    }
                }

                Direction.LEFT -> {
                    when (currentDirection) {
                        Direction.LEFT -> {
                            updateField(analyzed, currentPosition, -1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 0, FieldType.LEFT)
                        }

                        Direction.UP -> {
                            updateField(analyzed, currentPosition, -1, 1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 0, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 1, -1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 0, -1, FieldType.LEFT)
                            rightBias += 1
                        }

                        Direction.DOWN -> {
                            updateField(analyzed, currentPosition, -1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 0, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 1, FieldType.LEFT)
                            rightBias -= 1
                        }

                        else -> {}
                    }
                }

                Direction.RIGHT -> {
                    when (currentDirection) {
                        Direction.RIGHT -> {
                            updateField(analyzed, currentPosition, 1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, 0, FieldType.LEFT)
                        }

                        Direction.UP -> {
                            updateField(analyzed, currentPosition, 1, 0, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 1, 1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, 0, 1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, -1, FieldType.LEFT)
                            rightBias -= 1
                        }

                        Direction.DOWN -> {
                            updateField(analyzed, currentPosition, 1, -1, FieldType.RIGH)
                            updateField(analyzed, currentPosition, -1, 0, FieldType.LEFT)
                            updateField(analyzed, currentPosition, -1, 1, FieldType.LEFT)
                            updateField(analyzed, currentPosition, 0, 1, FieldType.LEFT)
                            rightBias += 1
                        }

                        else -> {}
                    }
                }
            }

            currentPosition += currentDirection.movement
            analyzed.set(currentPosition.row, currentPosition.column, FieldType.LOOP)
        }
        val lookingFor = if (rightBias > 0) FieldType.RIGH else FieldType.LEFT
        analyzed.forEach { row, column, value ->
            if (value == lookingFor) {
                analyzed.floodFill(Pair(row, column), lookingFor)
            }
        }
        return analyzed.count { it == lookingFor }
    }

    private fun loadDiagram(input: InputData): Pair<TwoDimensionalArray<Set<Direction>>, Coordinate> {
        val pipeTypes = mapOf(
            '-' to setOf(Direction.LEFT, Direction.RIGHT),
            '|' to setOf(Direction.UP, Direction.DOWN),
            'L' to setOf(Direction.UP, Direction.RIGHT),
            'J' to setOf(Direction.UP, Direction.LEFT),
            '7' to setOf(Direction.DOWN, Direction.LEFT),
            'F' to setOf(Direction.DOWN, Direction.RIGHT),
            '.' to emptySet()
        )

        val diagram = TwoDimensionalArray.charsFromLines(input.lines())
        val startingPosition = diagram.first { it == 'S' }

        val startingDirections = Direction.entries
            .filter { direction ->
                val neighborPosition = startingPosition + direction.movement
                val neighborDirections = pipeTypes.getValue(diagram.getOrNull(neighborPosition) ?: '.')
                direction.inverse() in neighborDirections
            }
            .toSet()

        return Pair(
            diagram.map {
                if (it == 'S') {
                    startingDirections
                } else {
                    pipeTypes[it] ?: emptySet()
                }
            },
            startingPosition
        )
    }

    private fun updateField(
        field: Mutable2dArray<FieldType?>,
        currentPosition: Coordinate,
        rowOffset: Int,
        columnOffset: Int,
        value: FieldType
    ) {
        val targetRow = currentPosition.row + rowOffset
        val targetColumn = currentPosition.column + columnOffset
        if (field.getSafe(targetRow, targetColumn) != FieldType.LOOP) {
            field.setSafe(targetRow, targetColumn, value)
        }
    }

    data class Step(
        val position: Coordinate,
        val direction: Direction
    ) {
        fun nextPosition(): Coordinate {
            return position + direction.movement
        }
    }

    enum class Direction(val movement: Pair<Int, Int>) {
        UP(Pair(-1, 0)),
        DOWN(Pair(1, 0)),
        LEFT(Pair(0, -1)),
        RIGHT(Pair(0, 1));

        fun inverse(): Direction {
            return when (this) {
                UP -> DOWN
                DOWN -> UP
                LEFT -> RIGHT
                RIGHT -> LEFT
            }
        }
    }

    enum class FieldType {
        LOOP, LEFT, RIGH  /* TODO */
    }

}