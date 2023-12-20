package cz.judas.jan.advent.year2023

import com.google.common.collect.Multimap
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.toMultiMap

object Day20 {
    @Answer("")
    fun part1(input: InputData): Long {
        val parser = parserFor<ModuleConfiguration>()
        val moduleConfigurations = input.lines()
            .map(parser::parse)
            .associateBy { it.name }
        val invertedGraph = moduleConfigurations
            .values
            .flatMap { module -> module.targets.map { it to module.name } }
            .toMultiMap()

        val modules = moduleConfigurations.mapValues { it.value.create(invertedGraph) }

        var numLow = 0L
        var numHigh = 0L
        for (i in 1..1000) {
            val backlog = ArrayDeque<Pulse>()
            backlog += Pulse(PulseType.LOW, "button", "broadcaster")
            while (backlog.isNotEmpty()) {
                val (pulseType, source, target) = backlog.removeFirst()
                if (pulseType == PulseType.LOW) {
                    numLow += 1
                } else {
                    numHigh += 1
                }
                modules[target]?.let { module ->
                    backlog += module.handlePulse(pulseType, source).map { Pulse(it.type, target, it.target) }
                }
            }
        }
        return numLow * numHigh
    }

    @Answer("")
    fun part2(input: InputData): Long {
        return 0L
    }

    @Pattern("([^ ]+) -> (.*)")
    data class ModuleConfiguration(
        val module: ModuleDefinition,
        val targets: @SplitOn(", ") List<String>
    ) {
        val name get(): String = module.name

        fun create(invertedGraph: Multimap<String, String>): Module {
            return module.create(targets, invertedGraph)
        }
    }

    sealed interface ModuleDefinition {
        val name: String

        fun create(targets: List<String>, invertedGraph: Multimap<String, String>): Module

        @Pattern("%(.+)")
        data class FlipFlop(override val name: String): ModuleDefinition {
            override fun create(targets: List<String>, invertedGraph: Multimap<String, String>): Module {
                return FlipFlopModule(targets)
            }
        }

        @Pattern("&(.+)")
        data class Conjunction(override val name: String): ModuleDefinition {
            override fun create(targets: List<String>, invertedGraph: Multimap<String, String>): Module {
                return ConjunctionModule(invertedGraph.asMap().getValue(name), targets)
            }
        }

        @Pattern("(broadcaster)")
        data class Broadcast(override val name: String): ModuleDefinition {
            override fun create(targets: List<String>, invertedGraph: Multimap<String, String>): Module {
                return BroadcastModule(targets)
            }
        }
    }

    interface Module {
        fun handlePulse(type: PulseType, source: String): List<OutgoingPulse>
    }

    class FlipFlopModule(private val targets: List<String>): Module {
        private var state = State.OFF

        override fun handlePulse(type: PulseType, source: String): List<OutgoingPulse> {
            return when (type) {
                PulseType.HIGH -> emptyList()
                PulseType.LOW -> {
                    val outgoingType = when (state) {
                        State.OFF -> {
                            state = State.ON
                            PulseType.HIGH
                        }
                        State.ON -> {
                            state = State.OFF
                            PulseType.LOW
                        }
                    }
                    targets.map { OutgoingPulse(outgoingType, it) }
                }
            }
        }

        enum class State { ON, OFF }
    }

    class ConjunctionModule(
        sources: Iterable<String>,
        private val targets: List<String>
    ): Module {
        private val history = sources.associateWith { PulseType.LOW }.toMutableMap()

        override fun handlePulse(type: PulseType, source: String): List<OutgoingPulse> {
            history[source] = type
            val outgoingType = if (history.values.all { it == PulseType.HIGH }) {
                PulseType.LOW
            } else {
                PulseType.HIGH
            }
            return targets.map { OutgoingPulse(outgoingType, it) }
        }
    }

    class BroadcastModule(private val targets: List<String>): Module {
        override fun handlePulse(type: PulseType, source: String): List<OutgoingPulse> {
            return targets.map { OutgoingPulse(type, it) }
        }
    }

    data class OutgoingPulse(val type: PulseType, val target: String)

    enum class PulseType { HIGH, LOW }

    data class Pulse(val type: PulseType, val source: String, val target: String)
}
