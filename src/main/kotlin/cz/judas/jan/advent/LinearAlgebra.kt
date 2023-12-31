package cz.judas.jan.advent

import java.math.BigInteger


fun solveLinearSystem(leftHandSide: Matrix, rightHandSide: Vector): Vector? {
    val matrix = leftHandSide.toMutable(listOf(rightHandSide))
    for (i in 0..<matrix.numRows) {
        if (matrix[i, i] != Fraction.ONE) {
            for (j in i..<matrix.numRows) {
                if (matrix[j, i] == Fraction.ONE) {
                    matrix.swapRows(i, j)
                    break
                }
            }
        }
        if (matrix[i, i] != Fraction.ONE) {
            for (j in i..<matrix.numRows) {
                if (matrix[j, i] != Fraction.ZERO) {
                    matrix.divideRow(j, matrix[j, i])
                    matrix.swapRows(i, j)
                    break
                }
            }
        }
        if (matrix[i, i] != Fraction.ONE) {
            return null
        }
        for (j in (i + 1)..<matrix.numRows) {
            matrix.addRowMultiple(i, j, -matrix[j, i])
        }
    }

    for (i in (matrix.numRows - 1) downTo 0) {
        for (j in 0..<i) {
            matrix.addRowMultiple(i, j, -matrix[j, i])
        }
    }

    return matrix.lastColumn()
}

class Matrix(private vararg val rows: List<Fraction>) {
    constructor(rows: List<List<Fraction>>): this(*rows.toTypedArray())

    fun toMutable(extraColumns: List<Vector>): MutableMatrix {
        return MutableMatrix(
            List(rows.size) { index ->
                val row = rows[index]
                val mutableRow = mutableListOf<Fraction>()
                mutableRow.addAll(row)
                extraColumns.forEach { extraColumn -> mutableRow += extraColumn[index] }
                mutableRow
            }
        )
    }
}

class MutableMatrix private constructor(private val rows: MutableList<Row>) {
    constructor(rows: Iterable<List<Fraction>>) : this(
        rows
            .map { row -> Row(row.toMutableList()) }
            .toMutableList()
    )

    val numRows: Int get() = rows.size

    operator fun get(row: Int, column: Int): Fraction = rows[row][column]

    fun swapRows(index1: Int, index2: Int) {
        val tmp = rows[index1]
        rows[index1] = rows[index2]
        rows[index2] = tmp
    }

    fun divideRow(row: Int, divisor: Fraction) {
        rows[row].divide(divisor)
    }

    fun addRowMultiple(sourceRowIndex: Int, targetRowIndex: Int, multiplier: Fraction) {
        if (multiplier != Fraction.ZERO) {
            rows[targetRowIndex].addMultiplied(rows[sourceRowIndex], multiplier)
        }
    }

    fun lastColumn(): Vector {
        return Vector(List(rows.size) { i -> rows[i].last() })
    }

    private class Row(private val values: MutableList<Fraction>) {
        operator fun get(index: Int): Fraction = values[index]

        fun divide(divisor: Fraction) {
            for (i in values.indices) {
                values[i] /= divisor
            }
        }

        fun addMultiplied(other: Row, multiplier: Fraction) {
            for (i in values.indices) {
                values[i] += other.values[i] * multiplier
            }
        }

        fun last(): Fraction = values.last()
    }
}

class Vector(private vararg val values: Fraction) {
    constructor(values: List<Fraction>) : this(*values.toTypedArray())

    operator fun get(index: Int): Fraction {
        return values[index]
    }

    override fun toString(): String {
        return values.contentToString()
    }
}

class Fraction private constructor(private val numerator: BigInteger, private val denominator: BigInteger): Comparable<Fraction> {
    override fun equals(other: Any?): Boolean {
        return other is Fraction && numerator == other.numerator && denominator == other.denominator
    }

    override fun hashCode(): Int = numerator.hashCode() + 31 * denominator.hashCode()

    override fun toString(): String = "${numerator}/${denominator}"

    operator fun plus(other: Fraction): Fraction = create(numerator * other.denominator + other.numerator * denominator, denominator * other.denominator)

    operator fun minus(other: Fraction): Fraction = create(numerator * other.denominator - other.numerator * denominator, denominator * other.denominator)

    operator fun unaryMinus(): Fraction = create(-numerator, denominator)

    operator fun times(other: Fraction): Fraction = create(numerator * other.numerator, denominator * other.denominator)

    operator fun times(other: Long): Fraction = times(other.toFraction())

    operator fun div(other: Fraction): Fraction = create(numerator * other.denominator, denominator * other.numerator)

    fun toDouble(): Double = numerator.toDouble() / denominator.toLong()

    fun toLong(): Long {
        if (denominator == BigInteger.ONE) {
            return numerator.toLong()
        } else {
            throw RuntimeException("Cannot convert non-integer fraction ${this} to long")
        }
    }

    override fun compareTo(other: Fraction): Int {
        val result = (numerator * other.denominator).compareTo(other.numerator * denominator)
        return if (denominator * other.denominator < BigInteger.ZERO) {
            -result
        } else {
            result
        }
    }

    companion object {
        val ZERO = Fraction(BigInteger.ZERO, BigInteger.ONE)
        val ONE = Fraction(BigInteger.ONE, BigInteger.ONE)

        fun create(numerator: BigInteger, denominator: BigInteger): Fraction {
            return if (numerator == BigInteger.ZERO) {
                ZERO
            } else if (denominator < BigInteger.ZERO) {
                return create(-numerator, -denominator)
            } else {
                val gcd = numerator.gcd(denominator)
                Fraction(numerator / gcd, denominator / gcd)
            }
        }

        fun fromLong(value: Long): Fraction {
            return Fraction(BigInteger.valueOf(value), BigInteger.ONE)
        }
    }
}

fun Long.toFraction(): Fraction = Fraction.fromLong(this)

fun Iterable<Fraction>.sum(): Fraction {
    return reduce(Fraction::plus)
}