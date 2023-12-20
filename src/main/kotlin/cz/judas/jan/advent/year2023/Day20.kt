package cz.judas.jan.advent.year2023

import com.google.common.collect.Multimap
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.Pattern
import cz.judas.jan.advent.SplitOn
import cz.judas.jan.advent.breadthFirstSearch
import cz.judas.jan.advent.getOnlyElement
import cz.judas.jan.advent.leastCommonMultiple
import cz.judas.jan.advent.parserFor
import cz.judas.jan.advent.toMultiMap

object Day20 {
    @Answer("703315117")
    fun part1(input: InputData): Long {
        val moduleGraphFactory = parseInput(input)

        val modules = moduleGraphFactory.create()

        var numLow = 0L
        var numHigh = 0L
        for (i in 1..1000) {
            breadthFirstSearch(Pulse(PulseType.LOW, "button", "broadcaster")) { (pulseType, source, target), backlog ->
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

    @Answer("230402300925361")
    fun part2(input: InputData): Long {
        val moduleGraphFactory = parseInput(input)

        val subgraphEndpoints = moduleGraphFactory.sourcesFor(moduleGraphFactory.sourcesFor("rx").getOnlyElement())

//        PrintWriter(File("/tmp/day20")).use { writer ->
//            writer.println("strict digraph {")
////            moduleConfigurations.keys.forEach{
////                writer.println("  ${it}")
////            }
////            writer.println()
//            moduleConfigurations.forEach{ name, module ->
//                writer.println("  ${name} -> {${module.targets.joinToString(" ")}}")
//            }
//            writer.println("}")
//        }

        return subgraphEndpoints.map { subgraphEndpoint ->
            val fakeRxModule = RxModule()
            val modules = moduleGraphFactory.create() + mapOf(subgraphEndpoint to fakeRxModule)

            var counter = 0L
            while (fakeRxModule.peekAndReset() != 1) {
                counter += 1

                breadthFirstSearch(Pulse(PulseType.LOW, "button", "broadcaster")) { (pulseType, source, target), backlog ->
                    modules[target]?.let { module ->
                        backlog += module.handlePulse(pulseType, source).map { Pulse(it.type, target, it.target) }
                    }
                }
            }
            counter
        }.leastCommonMultiple()
    }

    private fun parseInput(input: InputData): ModuleGraphFactory {
        val parser = parserFor<ModuleConfiguration>()
        return ModuleGraphFactory(input.lines().map(parser::parse))
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

    class RxModule: Module {
        var numLowPulses = 0

        override fun handlePulse(type: PulseType, source: String): List<OutgoingPulse> {
            if (type == PulseType.LOW) {
                numLowPulses += 1
            }
            return emptyList()
        }

        fun peekAndReset(): Int {
            val copy = numLowPulses
            numLowPulses = 0
            return copy
        }
    }

    class ModuleGraphFactory(modules: Iterable<ModuleConfiguration>) {
        private val moduleConfigurations = modules.associateBy { it.name }
        private val invertedGraph = moduleConfigurations
            .values
            .flatMap { module -> module.targets.map { it to module.name } }
            .toMultiMap()

        fun create(): Map<String, Module> {
            return moduleConfigurations.mapValues { it.value.create(invertedGraph) }
        }

        fun sourcesFor(moduleName: String): List<String> {
            return invertedGraph.get(moduleName).toList()
        }
    }
}
