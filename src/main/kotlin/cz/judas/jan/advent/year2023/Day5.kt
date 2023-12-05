package cz.judas.jan.advent.year2023

import com.google.common.collect.Ordering
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.parse
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.splitOn
import java.util.*

object Day5 {
    fun part1(input: InputData): Long {
        val transformationMapParser = parserFor<TransformationMap2>()
        val transformationRangeParser = parserFor<TransformationRange>()
        val chunks = input.lines()
            .splitOn { it.isEmpty() }

        val sourceSeeds = chunks[0][0].parse(parserFor<SeedsPart>()).seeds
        val transformationMaps = chunks.drop(1)
            .map { chunk ->
                val partialMap = chunk[0].parse(transformationMapParser)
                val ranges = chunk.drop(1).map { it.parse(transformationRangeParser) }
                TransformationMap(partialMap.sourceType, partialMap.destinationType, ranges)
            }

        val almanac = Almanac(sourceSeeds, transformationMaps)

        val transformers = almanac.maps.map { it.transformer() }
        return almanac.seeds.minOf { seed ->
            transformers.fold(seed) { value, map -> map[value] }
        }
    }

    fun part2(input: InputData): Long {
        val transformationMapParser = parserFor<TransformationMap2>()
        val transformationRangeParser = parserFor<TransformationRange>()
        val chunks = input.lines()
            .splitOn { it.isEmpty() }

        val sourceSeedRanges = chunks[0][0].parse(parserFor<SeedRanges>()).seeds.map { it.toRange() }
        val transformationMaps = chunks.drop(1)
            .map { chunk ->
                val partialMap = chunk[0].parse(transformationMapParser)
                val ranges = chunk.drop(1).map { it.parse(transformationRangeParser) }
                TransformationMap(partialMap.sourceType, partialMap.destinationType, ranges)
            }

        val transformers = transformationMaps.map { it.transformer() }
        return transformers
            .fold(sourceSeedRanges) { ranges, transformer ->
                ranges.flatMap { transformer[it] }
            }
            .minOf { it.start }
    }

    @Pattern("seeds: (.+)")
    data class SeedsPart(
        val seeds: @SplitOn(" ") List<Long>
    )

    @Pattern("seeds: (.+)")
    data class SeedRanges(
        val seeds: List<@Pattern("\\d+ \\d+") SeedRange>
    )

    @Pattern("(\\d+) (\\d+)")
    data class SeedRange(val start: Long, val length: Long) {
        fun toRange(): BetterRange<Long> = BetterRange(start, start + length)
    }

    @Pattern("(\\w+)-to-(\\w+) map:") // TODO
    data class TransformationMap2(
        val sourceType: String,
        val destinationType: String,
//        val ranges: List<TransformationRange>
    )

    data class TransformationMap(
        val sourceType: String,
        val destinationType: String,
        val ranges: List<TransformationRange>
    ) {
        fun transformer(): Transformer {
            return Transformer(
                ranges
                    .map {
                        BetterRange(it.sourceStart, it.sourceStart + it.length) to it.destinationStart - it.sourceStart
                    }
                    .let { RangeMap.fromPairs(it, 0L) }
            )
        }
    }

    @Pattern("(\\d+) (\\d+) (\\d+)")
    data class TransformationRange(
        val destinationStart: Long,
        val sourceStart: Long,
        val length: Long
    )

    @Pattern("seeds: (.+?)\n\n(.*)", multiline = true)
    data class Almanac(
        val seeds: @SplitOn(" ") List<Long>,
        val maps: @SplitOn("\n\n") List<TransformationMap>
    )

    class Transformer(
        private val mapping: RangeMap<Long, Long>
    ) {
        operator fun get(input: Long): Long {
            return input + mapping[input]
        }

        operator fun get(range: BetterRange<Long>): List<BetterRange<Long>> {
            return mapping.entriesFor(range)
                .map { BetterRange(it.first.start + it.second, it.first.endExclusive + it.second) }
        }
    }

    data class BetterRange<T>(val start: T, val endExclusive: T)

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