package cz.judas.jan.advent

import com.google.common.collect.Multiset


fun solveLinearSystem(equations: List<SymbolicEquation>): Map<Variable, Double>? {
    val allCoefficients = equations.map { it.coefficients() }
    val allVariables = allCoefficients.flatMap { equation -> equation.keys.flatMap { it.asMap().keys } }.toSet().toList()

    val linearSystem = allCoefficients.map { equation ->
        val linearCoefficients = equation.filterKeys { it.isNotEmpty() }.mapKeys { it.key.getOnlyElement() }
        val matrixRow = allVariables.map { linearCoefficients[it] ?: 0.0 }
        val rightHandSide = equation.entries.firstOrNull { it.key.isEmpty() }?.let { -it.value } ?: 0.0
        matrixRow to rightHandSide
    }
    val leftHandSide = Matrix(*linearSystem.map { it.first.toDoubleArray() }.toTypedArray())
    val rightHandSide = Vector(*linearSystem.map { it.second }.toDoubleArray())
    val solution = solveLinearSystem(leftHandSide, rightHandSide)
    return solution?.let { allVariables.mapIndexed { index, variable -> variable to solution[index] }.toMap() }
}


class SymbolicEquation(
    private val leftHandSide: Term,
    private val rightHandSide: Term
) {
    override fun toString(): String = "${leftHandSide} = ${rightHandSide}"

    operator fun minus(other: SymbolicEquation): SymbolicEquation {
        return SymbolicEquation(leftHandSide - other.leftHandSide, rightHandSide - other.rightHandSide)
    }

    fun coefficients(): Map<Multiset<Variable>, Double> {
        return (leftHandSide - rightHandSide).coefficients()
    }
}

interface Term {
    operator fun unaryMinus(): Term = Negation(this)

    operator fun minus(other: Term): Term = Sum(listOf(this, -other))

    operator fun minus(other: Long): Term = minus(Constant(other))

    operator fun plus(other: Term): Term = Sum(listOf(this, other))

    operator fun times(other: Term): Term = Product(listOf(this, other))

    operator fun times(other: Long): Term = times(Constant(other))

    fun coefficients(): Map<Multiset<Variable>, Double>

    fun priority(): Int = 0

    fun isCompound(): Boolean = false

    fun isNegative(): Boolean = false
}

data class Variable(val name: String) : Term {
    override fun coefficients(): Map<Multiset<Variable>, Double> = mapOf(multiSetOf(this) to 1.0)

    override fun toString(): String = name
}

data class Constant(val value: Double) : Term {
    constructor(value: Long) : this(value.toDouble())

    override fun unaryMinus(): Term = Constant(-value)

    override fun coefficients(): Map<Multiset<Variable>, Double> = mapOf(emptyMultiSet<Variable>() to value)

    override fun isNegative(): Boolean = value < 0

    override fun toString(): String = value.toString()
}

data class Sum(val terms: List<Term>) : Term {
    override fun unaryMinus(): Term = Sum(terms.map { -it })

    override fun minus(other: Term): Term {
        return Sum(terms + (-other))
    }

    override fun plus(other: Term): Term {
        return Sum(terms + other)
    }

    override fun coefficients(): Map<Multiset<Variable>, Double> = terms
        .flatMap { it.coefficients().toList() }
        .toMergedMap { it.sum() }
        .filterValues { it != 0.0 }

    override fun isCompound(): Boolean = true

    override fun toString(): String = terms[0].toString() + terms.subList(1).joinToString("") { if (it.isNegative()) " - ${-it}" else " + ${it}" }
}

data class Product(val terms: List<Term>) : Term {
    override fun unaryMinus(): Term {
        return Product(buildList {
            add(-terms[0])
            addAll(terms.subList(1))
        })
    }

    override fun times(other: Term): Term {
        return Product(terms + other)
    }

    override fun coefficients(): Map<Multiset<Variable>, Double> {
        return coefficients(0, multiSetOf(), 1.0)
    }

    override fun priority(): Int = 1

    override fun isCompound(): Boolean = true

    override fun isNegative(): Boolean = terms[0].isNegative()

    override fun toString(): String {
        return terms.joinToString(" * ") { if (it.priority() == 0 && it.isCompound()) "(${it})" else it.toString() }
    }

    private fun coefficients(layer: Int, baseVariables: Multiset<Variable>, baseCoefficient: Double): Map<Multiset<Variable>, Double> {
        val result = mutableMapOf<Multiset<Variable>, Double>()
        terms[layer].coefficients().forEach { (variables, coefficient) ->
            if (layer == terms.size - 1) {
                result[baseVariables + variables] = baseCoefficient * coefficient
            } else {
                coefficients(layer + 1, baseVariables + variables, baseCoefficient * coefficient).forEach { (nextVariables, nextPower) ->
                    result[nextVariables] = (result[nextVariables] ?: 1.0) * nextPower
                }
            }
        }
        return result
    }
}

data class Negation(val term: Term) : Term {
    override fun unaryMinus(): Term = term

    override fun coefficients(): Map<Multiset<Variable>, Double> = term.coefficients().mapValues { -it.value }

    override fun isNegative(): Boolean = true

    override fun toString(): String = "-${term}"
}
