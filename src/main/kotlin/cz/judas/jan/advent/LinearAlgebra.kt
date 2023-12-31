package cz.judas.jan.advent


fun solveLinearSystem(leftHandSide: Matrix, rightHandSide: Vector): Vector? {
    val matrix = leftHandSide.toMutable(listOf(rightHandSide))
    for (i in 0..<matrix.numRows) {
        if (matrix[i, i] != 1.0) {
            for (j in i..<matrix.numRows) {
                if (matrix[j, i] == 1.0) {
                    matrix.swapRows(i, j)
                    break
                }
            }
        }
        if (matrix[i, i] != 1.0) {
            for (j in i..<matrix.numRows) {
                if (matrix[j, i] != 0.0) {
                    matrix.divideRow(j, matrix[j, i])
                    matrix.swapRows(i, j)
                    break
                }
            }
        }
        if (matrix[i, i] != 1.0) {
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

class Matrix(private vararg val rows: DoubleArray) {
    operator fun times(vector: Vector): Vector {
        return Vector(*rows.map { vector.dotProduct(it) }.toDoubleArray())
    }

    fun toMutable(extraColumns: List<Vector>): MutableMatrix {
        return MutableMatrix(
            List(rows.size) { index ->
                val row = rows[index]
                val mutableRow = DoubleArray(row.size + extraColumns.size)
                row.copyInto(mutableRow)
                extraColumns.forEachIndexed { extraIndex, extraColumn -> mutableRow[row.size + extraIndex] = extraColumn[index] }
                mutableRow
            }
        )
    }
}

class MutableMatrix private constructor(private val rows: MutableList<Row>) {
    constructor(rows: Iterable<DoubleArray>) : this(rows.asSequence().map { Row(it) }.toMutableList())

    val numRows: Int get() = rows.size

    operator fun get(row: Int, column: Int): Double = rows[row][column]

    fun swapRows(index1: Int, index2: Int) {
        val tmp = rows[index1]
        rows[index1] = rows[index2]
        rows[index2] = tmp
    }

    fun divideRow(row: Int, divisor: Double) {
        rows[row].divide(divisor)
    }

    fun addRowMultiple(sourceRowIndex: Int, targetRowIndex: Int, multiplier: Double) {
        if (multiplier != 0.0) {
            rows[targetRowIndex].addMultiplied(rows[sourceRowIndex], multiplier)
        }
    }

    fun lastColumn(): Vector {
        return Vector(List(rows.size) { i -> rows[i].last() })
    }

    private class Row(private val values: DoubleArray) {
        operator fun get(index: Int): Double = values[index]

        fun divide(divisor: Double) {
            for (i in values.indices) {
                values[i] /= divisor
            }
        }

        fun addMultiplied(other: Row, multiplier: Double) {
            for (i in values.indices) {
                values[i] += other.values[i] * multiplier
            }
        }

        fun last(): Double = values.last()
    }
}

class Vector(private vararg val values: Double) {
    constructor(values: List<Double>) : this(*values.toDoubleArray())

    operator fun get(index: Int): Double {
        return values[index]
    }

    fun dotProduct(other: DoubleArray): Double {
        return values.zip(other).sumOf { it.first * it.second }
    }

    override fun toString(): String {
        return values.contentToString()
    }
}