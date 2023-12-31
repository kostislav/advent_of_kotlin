package cz.judas.jan.advent.year2023

import com.google.common.collect.Range
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.Constant
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SymbolicEquation
import cz.judas.jan.advent.Variable
import cz.judas.jan.advent.Vector
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.solveLinearSystem
import cz.judas.jan.advent.unorderedPairs

object Day24 {
    private val parser = parserFor<Hailstone>()

    @Answer("12783")
    fun part1(input: InputData): Int {
        val hailstones = input.lines()
            .map(parser::parse)
        val range = Range.closed(200000000000000.0, 400000000000000.0)
        return hailstones.unorderedPairs()
            .count { (first, second) ->
                first.futureIntersectionWith(second)?.let { range.contains(it[0]) && range.contains(it[1]) } ?: false
            }
    }

    @Answer("948485822969419")
    fun part2(input: InputData): Long {
        val hailstones = input.lines()
            .map(parser::parse)
            .take(3)

        val px = Variable("px")
        val py = Variable("py")
        val pz = Variable("pz")
        val vx = Variable("vx")
        val vy = Variable("vy")
        val vz = Variable("vz")

        val equations = hailstones
            .map { hailstone ->
                listOf(
                    SymbolicEquation((py - hailstone.position.y) * (vx - hailstone.velocity.x), (px - hailstone.position.x) * (vy - hailstone.velocity.y)),
                    SymbolicEquation((pz - hailstone.position.z) * (vx - hailstone.velocity.x), (px - hailstone.position.x) * (vz - hailstone.velocity.z)),
                    SymbolicEquation((py - hailstone.position.y) * (vz - hailstone.velocity.z), (pz - hailstone.position.z) * (vy - hailstone.velocity.y)),
                )
            }
            .zipWithNext()
            .flatMap { (equations1, equations2) -> equations1.zip(equations2).map { it.second - it.first } }

        val solution = solveLinearSystem(equations)!!

        return (solution.getValue(px) + solution.getValue(py) + solution.getValue(pz)).toLong()
    }

    @Pattern("(.+) @ (.+)")
    data class Hailstone(
        val position: Vector3d,
        val velocity: Vector3d
    ) {
        fun futureIntersectionWith(other: Hailstone): Vector? {
            val t1 = Variable("t1")
            val t2 = Variable("t2")
            val solution = solveLinearSystem(
                listOf(
                    SymbolicEquation(Constant(position.x) + t1 * velocity.x, Constant(other.position.x) + t2 * other.velocity.x),
                    SymbolicEquation(Constant(position.y) + t1 * velocity.y, Constant(other.position.y) + t2 * other.velocity.y)
                )
            )
            if (solution !== null) {
                val actualT1 = solution.getValue(t1)
                if (actualT1 > 0 && solution.getValue(t2) > 0) {
                    return Vector(
                        position.x + actualT1 * velocity.x,
                        position.y + actualT1 * velocity.y
                    )
                }
            }
            return null
        }
    }

    @Pattern("(-?\\d+), (-?\\d+), (-?\\d+)")
    data class Vector3d(val x: Long, val y: Long, val z: Long) {
        operator fun times(multiplier: Long): Vector3d {
            return Vector3d(x * multiplier, y * multiplier, z * multiplier)
        }

        operator fun plus(other: Vector3d): Vector3d {
            return Vector3d(x + other.x, y + other.y, z + other.z)
        }
    }
}
