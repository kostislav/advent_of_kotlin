package cz.judas.jan.advent.year2023

import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.parserFor

object Day19 {
    @Answer("476889")
    fun part1(input: InputData): Int {
        val parsedInput = parserFor<Input>().parse(input.asString())
        val workflows = parsedInput.workflows.associate { it.name to it.rules() }
        return parsedInput.parts
            .filter { part ->
                val outcome = generateSequence("in") {
                    workflows.getValue(it).firstNotNullOf { rule -> rule.process(part) }
                }
                    .first { it == "A" || it == "R" }
                outcome == "A"
            }
            .sumOf { it.ratingSum() }
    }

    @Answer("")
    fun part2(input: InputData): Long {
        return 0
    }

    @Pattern("(.+)\n\n(.+)")
    data class Input(
        val workflows: @SplitOn("\n") List<Workflow>,
        val parts: @SplitOn("\n") List<Part>
    )

    @Pattern("([a-z]+)\\{(.+)}")
    class Workflow(
        val name: String,
        private val rules: @SplitOn(",") List<String>
    ) {
        fun rules(): List<Rule> {
            return rules
                .map { rule ->
                    if (':' in rule) {
                        val groupValues = pattern.matchEntire(rule)!!.groupValues
                        Rule.Conditional(
                            groupValues[1][0],
                            Operator.fromString(groupValues[2]),
                            groupValues[3].toInt(),
                            groupValues[4]
                        )
                    } else {
                        Rule.Unconditional(rule)
                    }
                }
        }

        companion object {
            private val pattern = Regex("(.)(.)(\\d+):(.+)")
        }
    }

    sealed interface Rule {
        fun process(part: Part): String?

        @Pattern("(.)(.)(\\d+):(.+)")
        class Conditional(private val type: Char, private val operator: Operator, private val value: Int, private val target: String): Rule {
            override fun process(part: Part): String? {
                return if(operator.matches(part.ratings.getValue(type), value)) {
                    target
                } else {
                    null
                }
            }
        }

        @Pattern(".+")
        class Unconditional(private val target: String): Rule {
            override fun process(part: Part): String? {
                return target
            }
        }
    }

    enum class Operator {
        LESS_THAN {
            override fun matches(partValue: Int, threshold: Int) = partValue < threshold
        },
        GREATER_THAN {
            override fun matches(partValue: Int, threshold: Int) = partValue > threshold
        };

        abstract fun matches(partValue: Int, threshold: Int): Boolean

        companion object {
            fun fromString(input: String): Operator {
                return when(input) {
                    "<" -> LESS_THAN
                    ">" -> GREATER_THAN
                    else -> throw RuntimeException("Unexpected input")
                }
            }
        }
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
}