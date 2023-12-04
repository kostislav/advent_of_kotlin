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