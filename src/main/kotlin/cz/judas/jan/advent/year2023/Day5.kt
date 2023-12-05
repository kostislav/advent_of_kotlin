package cz.judas.jan.advent.year2023

import com.google.common.collect.Ordering
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.parserFor
import java.util.*

object Day5 {
    fun part1(input: InputData): Long {
        val parser = parserFor<Part1Almanac>()
        val almanac = parser.parse(input.asString())

        val transformers = almanac.maps.map { it.transformer() }
        return almanac.seeds.minOf { seed ->
            transformers.fold(seed) { value, map -> map[value] }
        }
    }

    fun part2(input: InputData): Long {
        val parser = parserFor<Part2Almanac>()
        val almanac = parser.parse(input.asString())

        val transformers = almanac.maps.map { it.transformer() }
        val sourceSeedRanges = almanac.seeds.map { it.toRange() }
        return transformers
            .fold(sourceSeedRanges) { ranges, transformer ->
                ranges.flatMap { transformer[it] }
            }
            .minOf { it.start }
    }

    @Pattern("seeds: (.+?)\n\n(.*)")
    data class Part1Almanac(
        val seeds: @SplitOn(" ") List<Long>,
        val maps: @SplitOn("\n\n") List<TransformationMap>
    )

    @Pattern("(\\w+)-to-(\\w+) map:\n(.*)")
    data class TransformationMap(
        val sourceType: String,
        val destinationType: String,
        val ranges: @SplitOn("\n") List<TransformationRange>
    ) {
        fun transformer(): Transformer {
            return Transformer(
                ranges
                    .map { it.toTransformerPair() }
                    .let { RangeMap.fromPairs(it, 0L) }
            )
        }
    }

    @Pattern("(\\d+) (\\d+) (\\d+)")
    data class TransformationRange(
        val destinationStart: Long,
        val sourceStart: Long,
        val length: Long
    ) {
        fun toTransformerPair(): Pair<BetterRange<Long>, Long> {
            return BetterRange(sourceStart, sourceStart + length) to destinationStart - sourceStart
        }
    }

    @Pattern("seeds: (.+?)\n\n(.*)")
    data class Part2Almanac(
        val seeds: List<@Pattern("\\d+ \\d+") SeedRange>,
        val maps: @SplitOn("\n\n") List<TransformationMap>
    )

    @Pattern("(\\d+) (\\d+)")
    data class SeedRange(val start: Long, val length: Long) {
        fun toRange(): BetterRange<Long> = BetterRange(start, start + length)
    }

    class Transformer(
        private val mapping: RangeMap<Long, Long>
    ) {
        operator fun get(input: Long): Long {
            return input + mapping[input]
        }

        operator fun get(range: BetterRange<Long>): List<BetterRange<Long>> {
            return mapping.entriesFor(range)
                .map { it.first.offsetBy(it.second) }
        }
    }

    data class BetterRange<T>(val start: T, val endExclusive: T)

    fun BetterRange<Long>.offsetBy(delta: Long): BetterRange<Long> {
        return BetterRange(start + delta, endExclusive + delta)
    }

    class RangeMap<K : Comparable<K>, V>(
        private val values: NavigableMap<K, Pair<K, V>>,
        private val defaultValue: V
    ) {
        operator fun get(key: K): V {
            val possibleRange = values.floorEntry(key)?.value
            return if (possibleRange === null || possibleRange.first < key) {
                defaultValue
            } else {
                possibleRange.second
            }
        }

        fun entriesFor(range: BetterRange<K>): List<Pair<BetterRange<K>, V>> {
            val result = mutableListOf<Pair<BetterRange<K>, V>>()
            var current = range.start
            while (current < range.endExclusive) {
                val lower = values.floorEntry(current)
                if (lower === null || lower.value.first < current) {
                    val nextStart = values.ceilingKey(current) ?: range.endExclusive
                    result += BetterRange(current, nextStart) to defaultValue
                    current = nextStart
                } else {
                    if (current < lower.value.first) {
                        val nextStart = Ordering.natural<K>().min(lower.value.first, range.endExclusive)
                        result += BetterRange(current, nextStart) to lower.value.second
                        current = nextStart
                    } else {
                        val nextStart = values.ceilingKey(current) ?: range.endExclusive
                        result += BetterRange(current, nextStart) to defaultValue
                        current = nextStart
                    }
                }
            }
            return result
        }

        companion object {
            fun <K : Comparable<K>, V> fromPairs(
                pairs: Iterable<Pair<BetterRange<K>, V>>,
                defaultValue: V
            ): RangeMap<K, V> {
                val mapping = TreeMap<K, Pair<K, V>>()
                pairs.forEach { (range, value) -> mapping.put(range.start, range.endExclusive to value) }
                return RangeMap(mapping, defaultValue)
            }
        }
    }
}