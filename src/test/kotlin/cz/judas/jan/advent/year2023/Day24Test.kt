package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Vector
import org.hamcrest.Description
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.hamcrest.TypeSafeMatcher
import org.junit.jupiter.api.Test
import kotlin.math.abs

class Day24Test {
    @Test
    fun part1() {
        assertThat(hailstone(19, 13, 30, -2, 1, -2).futureIntersectionWith(hailstone(18, 19, 22, -1, -1, -2)), closeTo(14.333, 15.333))
        assertThat(hailstone(19, 13, 30, -2, 1, -2).futureIntersectionWith(hailstone(12, 31, 28, -1, -2, -1)), closeTo(6.2, 19.4))
        assertThat(hailstone(19, 13, 30, -2, 1, -2).futureIntersectionWith(hailstone(20, 19, 15, 1, -5, -3)), nullValue())
        assertThat(hailstone(18, 19, 22, -1, -1, -2).futureIntersectionWith(hailstone(20, 25, 34, -2, -2, -4)), nullValue())
    }

    @Test
    fun part2() {
        val input = InputData.fromString(
            """
            19, 13, 30 @ -2, 1, -2
            18, 19, 22 @ -1, -1, -2
            20, 25, 34 @ -2, -2, -4
            12, 31, 28 @ -1, -2, -1
            20, 19, 15 @ 1, -5, -3
        """.trimIndent()
        )

        assertThat(Day24.part2(input), equalTo(47L))
    }

    private fun hailstone(x: Int, y: Int, z: Int, vx: Int, vy: Int, vz: Int): Day24.Hailstone {
        return Day24.Hailstone(Day24.Vector3d(x.toLong(), y.toLong(), z.toLong()), Day24.Vector3d(vx.toLong(), vy.toLong(), vz.toLong()))
    }

    private fun closeTo(expectedX: Double, expectedY: Double): Vector2dRealCloseToMatcher {
        return Vector2dRealCloseToMatcher(expectedX, expectedY)
    }

    private class Vector2dRealCloseToMatcher(
        private val expectedX: Double,
        private val expectedY: Double
    ): TypeSafeMatcher<Vector>() {
        override fun describeTo(description: Description) {
            description.appendText("(${expectedX}, ${expectedY})")
        }

        override fun matchesSafely(item: Vector): Boolean {
            return abs(item[0].toDouble() - expectedX) < 0.001 && abs(item[1].toDouble() - expectedY) < 0.001
        }
    }
}