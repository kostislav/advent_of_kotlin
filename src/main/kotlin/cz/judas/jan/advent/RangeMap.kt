package cz.judas.jan.advent

import com.google.common.collect.Ordering
import java.util.*

class RangeMap<K : Comparable<K>, V>(
    private val values: NavigableMap<K, Pair<K, V>>,
    private val defaultValue: V
) {
    operator fun get(key: K): V {
        val possibleRange = values.floorEntry(key)?.value
        return if (possibleRange === null || possibleRange.first < key) {
            defaultValue
        } else {
            possibleRange.second
        }
    }

    fun entriesFor(range: BetterRange<K>): List<Pair<BetterRange<K>, V>> {
        val result = mutableListOf<Pair<BetterRange<K>, V>>()
        var current = range.start
        while (current < range.endExclusive) {
            val lower = values.floorEntry(current)
            if (lower === null || lower.value.first <= current) {
                val nextStart = values.ceilingKey(current) ?: range.endExclusive
                result += BetterRange(current, nextStart) to defaultValue
                current = nextStart
            } else {
                val nextStart = Ordering.natural<K>().min(lower.value.first, range.endExclusive)
                result += BetterRange(current, nextStart) to lower.value.second
                current = nextStart
            }
        }
        return result
    }

    companion object {
        fun <K : Comparable<K>, V> fromPairs(
            pairs: Iterable<Pair<BetterRange<K>, V>>,
            defaultValue: V
        ): RangeMap<K, V> {
            val mapping = TreeMap<K, Pair<K, V>>()
            pairs.forEach { (range, value) -> mapping.put(range.start, range.endExclusive to value) }
            return RangeMap(mapping, defaultValue)
        }
    }
}

data class BetterRange<T>(val start: T, val endExclusive: T)

fun BetterRange<Long>.offsetBy(delta: Long): BetterRange<Long> {
    return BetterRange(start + delta, endExclusive + delta)
}
