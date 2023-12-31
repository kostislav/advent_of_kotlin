package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.BetterRange
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.RangeMap
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.offsetBy
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.unfold

object Day5 {
    @Answer("88151870")
    fun part1(input: InputData): Long {
        val parser = parserFor<Part1Almanac>()
        val almanac = parser.parse(input.asString())

        val transformers = transformerChain(almanac.maps)
        return almanac.seeds.minOf { seed ->
            transformers.fold(seed) { value, map -> map[value] }
        }
    }

    @Answer("2008785")
    fun part2(input: InputData): Long {
        val parser = parserFor<Part2Almanac>()
        val almanac = parser.parse(input.asString())

        val transformers = transformerChain(almanac.maps)
        val sourceSeedRanges = almanac.seeds.map { it.toRange() }
        return transformers
            .fold(sourceSeedRanges) { ranges, transformer ->
                ranges.flatMap { transformer[it] }
            }
            .minOf { it.start }
    }

    private fun transformerChain(maps: List<TransformationMap>): List<Transformer> {
        val mapsByDestinationType = maps.associateBy { it.destinationType }
        return unfold(mapsByDestinationType.getValue("location")) { mapsByDestinationType[it.sourceType] }
            .map { it.transformer() }
            .reversed()
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
}