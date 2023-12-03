package cz.judas.jan.advent

import org.ahocorasick.trie.Trie

fun String.splitInHalf(): Pair<String, String> {
    if (length % 2 == 0) {
        val half = length / 2
        return Pair(substring(0, half), substring(half))
    } else {
        throw RuntimeException("String has odd length: ${this}")
    }
}

fun String.characters(): List<Char> {
    return toCharArray().toList()
}

fun String.splitOnOnly(delimiter: String): Pair<String, String> {
    val parts = split(delimiter)
    if (parts.size == 2) {
        return Pair(parts[0], parts[1])
    } else {
        throw RuntimeException("Expected a single ${delimiter} delimiter in ${this}")
    }
}

fun String.pickByIndex(vararg indexes: Int): List<Char> {
    return indexes.map { get((it + length) % length) }
}

fun String.findAll(finder: StringFinder): List<Pair<String, Int>> {
    return finder.findAll(this)
}


class StringFinder(private val trie: Trie) {
    fun findAll(input: String): List<Pair<String, Int>> {
        return trie.parseText(input).map { emit -> Pair(emit.keyword, emit.start) }
    }

    companion object {
        fun forStrings(items: Set<String>, overlapping: Boolean): StringFinder {
            val builder = Trie.builder()
            if (!overlapping) {
                builder.ignoreOverlaps()
            }
            val trie = items.fold(builder, Trie.TrieBuilder::addKeyword).build()
            return StringFinder(trie)
        }
    }
}
