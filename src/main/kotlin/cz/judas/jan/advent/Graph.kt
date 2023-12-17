package cz.judas.jan.advent

import com.google.common.collect.HashMultimap


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