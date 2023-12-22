package cz.judas.jan.advent.year2023

import com.google.common.collect.Range
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.inverse
import cz.judas.jan.advent.mapParsing
import kotlin.math.abs
import kotlin.math.max

object Day22 {
    @Answer("407")
    fun part1(input: InputData): Int {
        val bricks = parseInput(input)
        val compacted = bricks.compact()
        val supportMap = compacted.supportMap()

        return compacted
            .count { brick -> supportMap.supportedBy(brick).all { supportMap.supporting(it).size > 1 } }
    }

    @Answer("59266")
    fun part2(input: InputData): Int {
        val bricks = parseInput(input)
        val compacted = bricks.compact()

        return compacted
            .sumOf { brick ->
                (compacted - brick).compact().numDifferentBricks(compacted)
            }
    }

    private fun parseInput(input: InputData): BrickLayout {
        val bricks = input.lines()
            .mapParsing("(.+)~(.*)") { startInclusive: Coordinate3d, endInclusive: Coordinate3d -> startInclusive to endInclusive }
            .associate { (startInclusive, endInclusive) ->
                val brick = Brick(
                    Range.closed(startInclusive.x, endInclusive.x),
                    Range.closed(startInclusive.y, endInclusive.y),
                    abs(startInclusive.z - endInclusive.z) + 1
                )
                val top = max(startInclusive.z, endInclusive.z)
                brick to top
            }
        val possibleSupports = bricks.mapValues { (brick, top) ->
            bricks.entries
                .filter { (otherBrick, otherTop) ->
                    otherTop <= top - brick.height && otherBrick.canTouch(brick)
                }
                .map { it.key }
                .toSet()
        }
        val bricksFromBottom = bricks.keys.sortedBy { it.bottom(bricks) }

        return BrickLayout(bricks, possibleSupports, bricksFromBottom)
    }

    class Brick(
        private val x: Range<Int>,
        private val y: Range<Int>,
        val height: Int,
    ) {
        fun canTouch(other: Brick): Boolean {
            return x.isConnected(other.x) && !x.intersection(other.x).isEmpty && y.isConnected(other.y) && !y.intersection(other.y).isEmpty
        }

        fun bottom(currentTops: Map<Brick, Int>): Int {
            return currentTops.getValue(this) - height + 1
        }
    }

    @Pattern("(\\d+),(\\d+),(\\d+)")
    data class Coordinate3d(val x: Int, val y: Int, val z: Int)

    private class BrickLayout(
        private val bricks: Map<Brick, Int>,
        private val possibleSupports: Map<Brick, Set<Brick>>,
        private val bricksFromBottom: List<Brick>
    ) : Iterable<Brick> by bricksFromBottom {
        fun compact(): BrickLayout {
            val compacted = bricks.toMutableMap()
            for (brick in bricksFromBottom) {
                val highestSupportTop = possibleSupports.getValue(brick).maxOfOrNull { compacted.getValue(it) } ?: 0
                compacted[brick] = highestSupportTop + brick.height
            }
            return BrickLayout(compacted, possibleSupports, bricksFromBottom)
        }

        fun supportMap(): SupportMap {
            val supportingBySupported = possibleSupports.mapValues { (supported, supporting) ->
                supporting.filter { supported.bottom(bricks) == bricks.getValue(it) + 1 }.toSet()
            }
            return SupportMap(supportingBySupported)
        }

        operator fun minus(brick: Brick): BrickLayout {
            return BrickLayout(
                bricks - brick,
                (possibleSupports - brick).mapValues { (_, value) -> value.filter { it != brick }.toSet() },
                bricksFromBottom.filter { it != brick }
            )
        }

        fun numDifferentBricks(other: BrickLayout): Int {
            return bricks.count { (key, value) -> other.bricks.getValue(key) != value }
        }
    }

    private class SupportMap(
        private val supportingBySupported: Map<Brick, Set<Brick>>
    ) {
        private val supportedBySupporting = supportingBySupported.inverse()

        fun supportedBy(brick: Brick): Set<Brick> {
            return supportedBySupporting[brick] ?: emptySet()
        }

        fun supporting(brick: Brick): Set<Brick> {
            return supportingBySupported.getValue(brick)
        }
    }
}
