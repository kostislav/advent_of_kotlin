package cz.judas.jan.advent.year2023

import com.google.common.collect.ContiguousSet
import com.google.common.collect.DiscreteDomain
import com.google.common.collect.Range
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.MutableMapWithDefault
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.mapParsing
import cz.judas.jan.advent.mutableMapWithDefault
import kotlin.math.abs
import kotlin.math.min

object Day22 {
    @Answer("407")
    fun part1(input: InputData): Int {
        val bricks = input.lines()
            .mapParsing("(.+)~(.*)") { startInclusive: Coordinate3d, endInclusive: Coordinate3d -> startInclusive to endInclusive }
            .mapIndexed { index, (startInclusive, endInclusive) -> index to parseBrick(startInclusive, endInclusive) }
            .toMap()
        val possibleContacts = bricks.mapValues { (_, brick) ->
            bricks.mapNotNull { (otherId, other) ->
                if (other !== brick && other.canTouch(brick)) {
                    otherId
                } else {
                    null
                }
            }.toSet()
        }
        val bricksPerZ = mutableMapWithDefault<Int, MutableSet<Int>> { mutableSetOf() }
        bricks.forEach { (id, brick) ->
            for (z in ContiguousSet.create(brick.zRange(), DiscreteDomain.integers()).asList()) {
                bricksPerZ.getOrCreate(z) += id
            }
        }

        compact(bricksPerZ, bricks, possibleContacts)
        return bricks.filter { (id, brick) ->
            val supportedBricks = bricksPerZ.getValue(brick.top() + 1).intersect(possibleContacts.getValue(id))
            supportedBricks
                .filter { supportedBrickId -> bricksPerZ.getValue(brick.top()).intersect(possibleContacts.getValue(supportedBrickId)).size == 1 }
                .isEmpty()
        }.size
    }

    @Answer("")
    fun part2(input: InputData): Int {
//        TODO dedup
        val bricks = input.lines()
            .mapParsing("(.+)~(.*)") { startInclusive: Coordinate3d, endInclusive: Coordinate3d -> startInclusive to endInclusive }
            .mapIndexed { index, (startInclusive, endInclusive) -> index to parseBrick(startInclusive, endInclusive) }
            .toMap()
        val possibleContacts = bricks.mapValues { (_, brick) ->
            bricks.mapNotNull { (otherId, other) ->
                if (other !== brick && other.canTouch(brick)) {
                    otherId
                } else {
                    null
                }
            }.toSet()
        }
        val bricksPerZ = mutableMapWithDefault<Int, MutableSet<Int>> { mutableSetOf() }
        bricks.forEach { (id, brick) ->
            for (z in ContiguousSet.create(brick.zRange(), DiscreteDomain.integers()).asList()) {
                bricksPerZ.getOrCreate(z) += id
            }
        }

        compact(bricksPerZ, bricks, possibleContacts)
        return bricks.keys
            .sumOf { brickId ->
                compact(
                    bricksPerZ.mapValues { (it - brickId).toMutableSet() },
                    bricks.filterKeys { it != brickId }.mapValues { it.value.copy() },
                    possibleContacts.filterKeys { it != brickId }
                        .mapValues { it.value - brickId }
                )
            }
    }

    private fun compact(
        bricksPerZ: MutableMapWithDefault<Int, MutableSet<Int>>,
        bricks: Map<Int, Brick>,
        possibleContacts: Map<Int, Set<Int>>
    ): Int {
        val fallenBrickIds = mutableSetOf<Int>()
        for (z in 2..bricksPerZ.keys.max()) {
            for (brickId in bricksPerZ.getOrCreate(z).toList()) {
                val brick = bricks.getValue(brickId)
                if (brick.bottom == z) {
                    val possibleBrickContacts = possibleContacts.getValue(brickId)
                    var currentZ = z
                    while (currentZ >= 2 && bricksPerZ.getValue(currentZ - 1).intersect(possibleBrickContacts).isEmpty()) {
                        bricksPerZ.getValue(currentZ - 1 + brick.height) -= brickId
                        bricksPerZ.getValue(currentZ - 1) += brickId
                        currentZ -= 1
                        brick.moveDown()
                        fallenBrickIds += brickId
                    }
                }
            }
        }
        return fallenBrickIds.size
    }

    private fun parseBrick(startInclusive: Coordinate3d, endInclusive: Coordinate3d): Brick {
        return Brick(
            Range.closed(startInclusive.x, endInclusive.x),
            Range.closed(startInclusive.y, endInclusive.y),
            abs(startInclusive.z - endInclusive.z) + 1,
            min(startInclusive.z, endInclusive.z)
        )
    }

    class Brick(
        val x: Range<Int>,
        val y: Range<Int>,
        val height: Int,
        var bottom: Int
    ) {
        fun canTouch(other: Brick): Boolean {
            return x.isConnected(other.x) && !x.intersection(other.x).isEmpty && y.isConnected(other.y) && !y.intersection(other.y).isEmpty
        }

        fun zRange(): Range<Int> {
            return Range.closed(bottom, bottom + height - 1)
        }

        fun moveDown() {
            bottom -= 1
        }

        fun top(): Int {
            return bottom + height - 1
        }

        fun copy(): Brick {
            return Brick(x, y, height, bottom)
        }
    }

    @Pattern("(\\d+),(\\d+),(\\d+)")
    data class Coordinate3d(
        val x: Int,
        val y: Int,
        val z: Int
    )
}
