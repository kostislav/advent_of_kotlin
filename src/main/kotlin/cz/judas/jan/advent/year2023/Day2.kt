package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.product
import cz.judas.jan.advent.splitOnOnly

object Day2 {
    fun part1(input: InputData): Int {
        val maxCubes = mapOf(
            "red" to 12,
            "green" to 13,
            "blue" to 14,
        )
        return input.lines()
            .sumOf { line ->
                val (header, games) = line.splitOnOnly(": ")
                val isPossible = games.split("; ").all { game ->
                    game.split(", ").all {
                        it.splitOnOnly(" ").let { (number, color) -> number.toInt() <= maxCubes.getValue(color) }
                    }
                }
                if (isPossible) header.splitOnOnly(" ").second.toInt() else 0
            }
    }

    fun part2(input: InputData): Int {
        return input.lines()
            .sumOf { line ->
                val (_, games) = line.splitOnOnly(": ")
                val maxCubes = games.split("; ").map { game ->
                    game.split(", ").map {
                        it.splitOnOnly(" ").let { (number, color) -> color to number.toInt() }
                    }
                        .toMap()
                }
                listOf("red", "green", "blue")
                    .map { color -> maxCubes.map { it[color] ?: 0 }.max() }
                    .product()
            }
    }
}