package cz.judas.jan.advent.year2023

import com.google.common.collect.Range
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.ParsedFromString
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.mapFirst
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.product
import cz.judas.jan.advent.size

object Day19 {
    @Answer("476889")
    fun part1(input: InputData): Int {
        val parsedInput = parserFor<Input>().parse(input.asString())
        val workflows = parsedInput.workflows.associateBy { it.name }
        return parsedInput.parts
            .filter { part ->
                val outcome = generateSequence("in") {
                    workflows.getValue(it).next(part)
                }.first { it == "A" || it == "R" }
                outcome == "A"
            }
            .sumOf { it.ratingSum() }
    }

    @Answer("132380153677887")
    fun part2(input: InputData): Long {
        val fullRange = Range.closed(1, 4000)
        val parser = parserFor<Workflow>()
        val workflows = input.lines()
            .takeWhile { it.isNotEmpty() }
            .map(parser::parse)
            .associateBy { it.name }

        val backlog = ArrayDeque<Pair<String, Hypercube>>()
        backlog += "in" to Hypercube(
            mapOf(
                'x' to fullRange,
                'm' to fullRange,
                'a' to fullRange,
                's' to fullRange
            )
        )
        val acceptedRanges = mutableListOf<Hypercube>()
        while (backlog.isNotEmpty()) {
            val (workflowId, remainingRange) = backlog.removeFirst()
            if (workflowId == "A") {
                acceptedRanges += remainingRange
            } else if (workflowId != "R") {
                backlog += workflows.getValue(workflowId).next(remainingRange)
            }
        }
        return acceptedRanges.sumOf { it.volume() }
    }

    @Pattern("(.+)\n\n(.+)")
    data class Input(
        val workflows: @SplitOn("\n") List<Workflow>,
        val parts: @SplitOn("\n") List<Part>
    )

    @Pattern("([a-z]+)\\{(?:(.+),)?([a-zA-Z]+)}")
    class Workflow(
        val name: String,
        private val conditionalRules: @SplitOn(",") List<ConditionalRule>,
        private val fallback: String
    ) {
        fun next(part: Part): String {
            return conditionalRules.firstNotNullOfOrNull { rule -> rule.process(part) } ?: fallback
        }

        fun next(input: Hypercube): List<Pair<String, Hypercube>> {
            val result = mutableListOf<Pair<String, Hypercube>>()
            var current = input
            for (conditionalRule in conditionalRules) {
                val (matching, nonMatching) = conditionalRule.split(current)
                if (matching !== null) {
                    result += matching
                }
                if (nonMatching === null) {
                    return result
                } else {
                    current = nonMatching
                }
            }
            result += fallback to current
            return result
        }
    }

    @Pattern("(.)(.)(\\d+):(.+)")
    class ConditionalRule(private val type: Char, operator: Operator, value: Int, private val target: String) {
        private val matchingRange = operator.matchingRange(value)
        private val nonMatchingRange = operator.nonMatchingRange(value)

        fun process(part: Part): String? {
            return if (matchingRange.contains(part.ratings.getValue(type))) {
                target
            } else {
                null
            }
        }

        fun split(input: Hypercube): Pair<Pair<String, Hypercube>?, Hypercube?> {
            return input.split(type, matchingRange, nonMatchingRange)
                .mapFirst { if (it === null) null else target to it }
        }
    }

    @Suppress("unused")
    enum class Operator(override val stringValue: String) : ParsedFromString {
        LESS_THAN("<") {
            override fun matchingRange(threshold: Int): Range<Int> = Range.lessThan(threshold)
            override fun nonMatchingRange(threshold: Int): Range<Int> = Range.atLeast(threshold)
        },
        GREATER_THAN(">") {
            override fun matchingRange(threshold: Int): Range<Int> = Range.greaterThan(threshold)
            override fun nonMatchingRange(threshold: Int): Range<Int> = Range.atMost(threshold)
        };

        abstract fun matchingRange(threshold: Int): Range<Int>
        abstract fun nonMatchingRange(threshold: Int): Range<Int>
    }

    @Pattern("\\{(.+)}")
    class Part(
        ratingsList: @SplitOn(",") List<Rating>
    ) {
        val ratings = ratingsList.associate { it.type to it.value }

        fun ratingSum(): Int = ratings.values.sum()
    }

    @Pattern("([a-z]+)=(\\d+)")
    data class Rating(
        val type: Char,
        val value: Int
    )

    class Hypercube(private val dimensions: Map<Char, Range<Int>>) {
        fun split(dimension: Char, matchingRange: Range<Int>, nonMatchingRange: Range<Int>): Pair<Hypercube?, Hypercube?> {
            val inputRange = dimensions.getValue(dimension)
            val matchingInput = inputRange.intersection(matchingRange)
            val nonMatchingInput = inputRange.intersection(nonMatchingRange)
            return Pair(
                if (matchingInput.isEmpty) null else Hypercube(dimensions + mapOf(dimension to matchingInput)),
                if (nonMatchingInput.isEmpty) null else Hypercube(dimensions + mapOf(dimension to nonMatchingInput))
            )
        }

        fun volume(): Long {
            return dimensions.values.map { it.size.toLong() }.product()
        }
    }
}
