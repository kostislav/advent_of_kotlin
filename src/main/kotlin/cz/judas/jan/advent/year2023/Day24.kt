package cz.judas.jan.advent.year2023

import com.google.common.collect.Range
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.cartesianProduct
import cz.judas.jan.advent.parserFor

object Day24 {
    @Answer("")
    fun part1(input: InputData): Int {
        val parser = parserFor<Hailstone>()
        val hailstones = input.lines()
            .map(parser::parse)
        val range = Range.closed(200000000000000.0, 400000000000000.0)
        return hailstones.cartesianProduct(hailstones)
            .count { (first, second) ->
                first.futureIntersectionWith(second)?.let { range.contains(it.x) && range.contains(it.y) } ?: false
            } / 2
    }

    @Answer("")
    fun part2(input: InputData): Int {
        return 0
    }

    @Pattern("(.+) @ (.+)")
    data class Hailstone(
        val position: Vector3d,
        val velocity: Vector3d
    ) {
        fun futureIntersectionWith(other: Hailstone): Vector2dReal? {
            val divisor = velocity.x * other.velocity.y - velocity.y * other.velocity.x
            return if (divisor == 0L) {
                null
            } else {
                val t1 = (other.velocity.y.toDouble() * (other.position.x - position.x) - other.velocity.x * (other.position.y - position.y)) / divisor
                if (t1 > 0) {
                    val t2 = (position.y + velocity.y * t1 - other.position.y) / other.velocity.y
                    if (t2 > 0) {
                        Vector2dReal(
                            position.x + t1 * velocity.x,
                            position.y + t1 * velocity.y
                        )
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        }
    }

    @Pattern("(-?\\d+), (-?\\d+), (-?\\d+)")
    data class Vector3d(val x: Long, val y: Long, val z: Long)

    data class Vector2dReal(val x: Double, val y: Double)
}
