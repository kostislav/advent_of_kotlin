package cz.judas.jan.advent

import com.google.common.collect.HashMultimap
import java.util.*
import kotlin.Comparator


class UndirectedGraph<T>(private val edges: HashMultimap<T, T>) {
    val nodes: Set<T> get() = edges.keySet()

    fun neighborsOf(node: T): Set<T> {
        return edges.get(node) ?: emptySet()
    }

    class Builder<T> {
        private val edges: HashMultimap<T, T> = HashMultimap.create()

        fun addEdge(node1: T, node2: T) {
            edges.put(node1, node2)
            edges.put(node2, node1)
        }

        fun build(): UndirectedGraph<T> {
            return UndirectedGraph(edges)
        }
    }

    companion object {
        fun <T> builder(): Builder<T> = Builder()
    }
}

enum class Direction(val movement: Vector2d) {
    UP(Vector2d(-1, 0)),
    DOWN(Vector2d(1, 0)),
    LEFT(Vector2d(0, -1)),
    RIGHT(Vector2d(0, 1));

    fun inverse(): Direction {
        return byVector.getValue(Vector2d(-movement.rows, -movement.columns))
    }

    fun move(how: RelativeDirection): Direction {
        return when(how) {
            RelativeDirection.FORWARD -> this
            RelativeDirection.BACK -> this.inverse()
            RelativeDirection.LEFT -> byVector.getValue(movement.rotateRight())
            RelativeDirection.RIGHT -> byVector.getValue(movement.rotateLeft())
        }
    }

    companion object {
        val byVector = Direction.entries.associateBy { it.movement }
    }
}

enum class RelativeDirection {
    FORWARD,
    BACK,
    LEFT,
    RIGHT
}

fun <N> shortestPath(startingNode: N, targetNode: N, edgeSupplier: (N) -> Map<N, Int>): Int {
    val queue = PriorityQueue<BacklogNode<N>>(Comparator.comparing { it.pathLength })
    val done = mutableSetOf<N>()
    queue += BacklogNode(startingNode, 0)

    while (queue.isNotEmpty()) {
        val (current, length) = queue.remove()
        if (current !in done) {
            if (current == targetNode) {
                return length
            }
            done += current
            for ((next, weight) in edgeSupplier(current)) {
                queue += BacklogNode(next, length + weight)
            }
        }
    }
    throw RuntimeException("Could not find path")
}

fun calculateArea(steps: List<PathSegment>): Long {
    val orientation = steps.cycle()
        .windowed(2) { it[0].direction.movement.rotateRight().dotProduct(it[1].direction.movement) }
        .take(steps.size)
        .sum()
    val positiveDirection = if (orientation > 0) Direction.LEFT else Direction.RIGHT
    val negativeDirection = positiveDirection.inverse()

    var currentRow = 0L
    var sum = 0L
    for (i in steps.indices) {
        val previous = steps[(i - 1 + steps.size) % steps.size]
        val current = steps[i]
        val previousRow = currentRow
        currentRow += current.direction.movement.rows * current.amount
        if (current.direction == positiveDirection) {
            sum += (current.amount - 1) * currentRow
            if (previous.direction == Direction.UP) {
                sum += currentRow
            }
        } else if(previous.direction == positiveDirection && current.direction == Direction.DOWN) {
            sum += previousRow
        } else if (current.direction == negativeDirection) {
            sum -= (current.amount - 1) * (currentRow + 1)
            if (previous.direction == Direction.DOWN) {
                sum -= currentRow + 1
            }
        } else if (previous.direction == negativeDirection && current.direction == Direction.UP) {
            sum -= previousRow + 1
        }
        sum += current.amount
    }
    return sum
}

data class PathSegment(val direction: Direction, val amount: Int)

private data class BacklogNode<T>(val node: T, val pathLength: Int)
