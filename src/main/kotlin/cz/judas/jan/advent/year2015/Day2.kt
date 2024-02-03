package cz.judas.jan.advent.year2015

import com.google.common.collect.Ordering
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.mapParsing

object Day2 {
    @Answer("1606483")
    fun part1(input: InputData): Int {
        return lineSum(input, ::part1)
    }

    @Answer("3842356")
    fun part2(input: InputData): Int {
        return lineSum(input, ::part2)
    }

    fun part1(present: Present): Int {
        return present.surfaceArea() + present.smallestSide().area()
    }

    fun part2(present: Present): Int {
        return present.smallestSide().perimeter() + present.volume()
    }

    private fun lineSum(input: InputData, transformation: (Present) -> Int): Int {
        return input.lines()
            .mapParsing("(\\d+)x(\\d+)x(\\d+)", ::Present)
            .sumOf(transformation)
    }

    data class Present(val a: Int, val b: Int, val c: Int) {
        fun surfaceArea(): Int = 2 * (a * b + b * c + a * c)

        fun smallestSide(): Side {
            val (smallestSideLength, secondSmallestSideLength) = Ordering.natural<Int>().leastOf(listOf(a, b, c), 2)
            return Side(smallestSideLength, secondSmallestSideLength)
        }

        fun volume(): Int = a * b * c
    }

    data class Side(val a: Int, val b: Int) {
        fun area(): Int = a * b

        fun perimeter(): Int = 2 * (a + b)
    }
}