package cz.judas.jan.advent.year2015

import com.google.common.collect.Ordering
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.product

object Day2 {
    val parser = parserFor<Present>()

    @Answer("1606483")
    fun part1(input: InputData): Int {
        return input.lines()
            .map(parser::parse)
            .sumOf(::part1)
    }

    fun part1(present: Present): Int {
        return present.surfaceArea() + present.smallestSide().area()
    }

    @Answer("3842356")
    fun part2(input: InputData): Int {
        return input.lines()
            .map(parser::parse)
            .sumOf(::part2)
    }

    fun part2(present: Present): Int {
        return present.smallestSide().perimeter() + present.volume()
    }

    @Pattern("(.+)")
    data class Present(val dimensions: @SplitOn("x") List<Int>) {
        fun surfaceArea(): Int {
            return 2 * (dimensions[0] * dimensions[1] + dimensions[1] * dimensions[2] + dimensions[2] * dimensions[0])
        }

        fun smallestSide(): Side {
            val (smallestSideLength, secondSmallestSideLength) = Ordering.natural<Int>().leastOf(dimensions, 2)
            return Side(smallestSideLength, secondSmallestSideLength)
        }

        fun volume(): Int = dimensions.product()
    }

    data class Side(val a: Int, val b: Int) {
        fun area(): Int = a * b

        fun perimeter(): Int = 2 * (a + b)
    }
}