package cz.judas.jan.advent.year2016

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Vector2d
import cz.judas.jan.advent.characters

object Day2 {
    fun part1(input: InputData): String {
        val keypad = mapOf(
            Vector2d(0, 2) to 1,
            Vector2d(1, 2) to 2,
            Vector2d(2, 2) to 3,
            Vector2d(0, 1) to 4,
            Vector2d(1, 1) to 5,
            Vector2d(2, 1) to 6,
            Vector2d(0, 0) to 7,
            Vector2d(1, 0) to 8,
            Vector2d(2, 0) to 9,
        )

        return input.lines()
            .scan(Vector2d(1, 1)) { current, instructions -> instructions.characters().fold(current, ::move) }
            .map { keypad.getValue(it) }
            .drop(1)
            .joinToString("")
    }

    private fun move(current: Vector2d, direction: Char): Vector2d {
        return when(direction) {
            'U' -> if (current.columns < 2) current.copy(columns = current.columns + 1) else current
            'D' -> if (current.columns > 0) current.copy(columns = current.columns - 1) else current
            'L' -> if (current.rows > 0) current.copy(rows = current.rows - 1) else current
            'R' -> if (current.rows < 2) current.copy(rows = current.rows + 1) else current
            else -> throw RuntimeException("Unexpected direction ${direction}")
        }
    }
}