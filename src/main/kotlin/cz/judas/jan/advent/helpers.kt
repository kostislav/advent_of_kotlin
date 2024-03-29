package cz.judas.jan.advent

import com.google.common.collect.BoundType
import com.google.common.collect.ContiguousSet
import com.google.common.collect.DiscreteDomain
import com.google.common.collect.Range
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.ceil
import kotlin.math.floor


fun Range<Double>.enclosedLongRange(): Range<Long> {
    val ceilStart = ceil(lowerEndpoint())
    val floorEnd = floor(upperEndpoint())

    return Range.closed(
        ceilStart.toLong() + if (hasLowerBound() && lowerBoundType() == BoundType.OPEN && ceilStart == lowerEndpoint()) 1 else 0,
        floorEnd.toLong() - if (hasUpperBound() && upperBoundType() == BoundType.OPEN && floorEnd == upperEndpoint()) 1 else 0
    )
}

fun Range<Long>.length(): Long {
    return upperEndpoint() - lowerEndpoint() + 1
}

fun Range<Int>.length(): Int {
    return upperEndpoint() - lowerEndpoint() + 1
}

fun Range<Int>.asSequence(): Sequence<Int> {
    return sequence {
        if (lowerBoundType() == BoundType.CLOSED) {
            yield(lowerEndpoint())
        }
        yieldAll(IntRange(lowerEndpoint() + 1, upperEndpoint() + 1))
        if (upperBoundType() == BoundType.CLOSED) {
            yield(upperEndpoint())
        }
    }
}

fun <I1, I2, O> recursive(input1: I1, input2: I2, cached: Boolean = false, calculation: (I1, I2, (I1, I2) -> O) -> O): O {
    return if (cached) {
        recursive(Pair(input1, input2), cached) { (next1, next2), recursion ->
            calculation(next1, next2) { rec1, rec2 ->
                recursion(Pair(rec1, rec2))
            }
        }
    } else {
        val recursiveHolder = AtomicReference<(I1, I2) -> O>()
        recursiveHolder.set { key1, key2 -> calculation(key1, key2, recursiveHolder.get()) }
        calculation(input1, input2, recursiveHolder.get())
    }
}

val Range<Int>.size get(): Int {
    return ContiguousSet.create(this, DiscreteDomain.integers()).size
}

fun <I, O> recursive(input: I, cached: Boolean = false, calculation: (I, (I) -> O) -> O): O {
    val cache = mutableMapOf<I, O>()
    val recursiveHolder = AtomicReference<(I) -> O>()
    if (cached) {
        recursiveHolder.set { key ->
            val cachedValue = cache.get(key)
            if (cachedValue === null) {
                val computedValue = calculation(key, recursiveHolder.get())
                cache.put(key, computedValue)
                computedValue
            } else {
                cachedValue
            }
        }
    } else {
        recursiveHolder.set { key -> calculation(key, recursiveHolder.get()) }
    }
    return calculation(input, recursiveHolder.get())
}


fun <T> breadthFirstSearch(startingPoint: T, step: (T, backlog: BreadthFirstSearchBacklog<T>) -> Unit) {
    val backlog = ArrayDeque<T>()
    val backlogWrapper = BreadthFirstSearchBacklog(backlog)
    backlog += startingPoint
    while (backlog.isNotEmpty()) {
        val next = backlog.removeFirst()
        step(next, backlogWrapper)
    }
}

class BreadthFirstSearchBacklog<T>(private val queue: ArrayDeque<T>) {
    operator fun plusAssign(item: T) {
        queue += item
    }

    operator fun plusAssign(items: Iterable<T>) {
        queue += items
    }
}


data class Vector2d(val rows: Int, val columns: Int) {
    fun rotateRight(): Vector2d {
        return Vector2d(columns, -rows)
    }

    fun rotateLeft(): Vector2d {
        return Vector2d(-columns, rows)
    }

    fun dotProduct(other: Vector2d): Int {
        return rows * other.rows + columns * other.columns
    }

    operator fun times(howMuch: Int): Vector2d {
        return Vector2d(rows * howMuch, columns * howMuch)
    }
}

enum class Digit {
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE;

    val value: Int get() = ordinal
}

@Target(AnnotationTarget.FUNCTION)
annotation class Answer(val value: String)