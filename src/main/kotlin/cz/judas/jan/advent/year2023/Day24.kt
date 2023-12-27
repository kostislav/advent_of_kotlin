package cz.judas.jan.advent.year2023

import com.google.common.collect.Range
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.intersection
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.unorderedPairs
import kotlin.math.ceil
import kotlin.reflect.KProperty

object Day24 {
    private val parser = parserFor<Hailstone>()

    @Answer("12783")
    fun part1(input: InputData): Int {
        val hailstones = input.lines()
            .map(parser::parse)
        val range = Range.closed(200000000000000.0, 400000000000000.0)
        return hailstones.unorderedPairs()
            .count { (first, second) ->
                first.futureIntersectionWith(second)?.let { range.contains(it.x) && range.contains(it.y) } ?: false
            }
    }

    @Answer("")
    fun part2(input: InputData): Long {
//    solved using sagemath:
//    a,b,c,d,e,f,g,h,k = var('a b c d e f g h k')
//
//    f1 = a+b*c-119566840879742-b*18
//    f2 = d+b*e-430566433235378+b*130
//    f3 = f+b*g-268387686114969-b*74
//    f4 = a+h*c-433973471892198+h*16
//    f5 = d+h*e-260061119249300+h*170
//    f6 = f+h*g-263051300077633+h*118
//    f7 = a+k*c-44446443386018-k*197
//    f8 = d+k*e-281342848485672-k*16
//    f9 = f+k*g-166638492241385-k*200
//
//    solve([f1==0,f2==0,f3==0,f4==0,f5==0,f6==0,f7==0,f8==0,f8==0],a,b,c,d,e,f,g,h,k)
        return 454587375941126L + 244764814652484L + 249133632375809L
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

        fun move(timeDelta: Long): Hailstone {
            return Hailstone(position + velocity * timeDelta, velocity)
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

    data class Vector2dReal(val x: Double, val y: Double)
}
