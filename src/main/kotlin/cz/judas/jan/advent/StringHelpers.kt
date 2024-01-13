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

fun <T> String.tokenize(tokenizer: StringTokenizer<T>): Sequence<Token<T>> {
    return tokenizer.tokenize(this)
}

fun <O> String.translate(lookup: Map<Char, O>): List<O> {
    return map { lookup.getValue(it) }
}

class StringTokenizer<T>(
    private val regex: Regex,
    private val groupProcessors: List<(String) -> T>
) {
    fun tokenize(input: String): Sequence<Token<T>> {
        return sequence {
            for (matchResult in regex.findAll(input)) {
                for (i in groupProcessors.indices) {
                    val group = matchResult.groups[i + 1]
                    if (group !== null) {
                        yield(Token(group.range, groupProcessors[i](group.value)))
                    }
                }
            }
        }
    }

    companion object {
        fun <T> create(patterns: Map<String, (String) -> T>): StringTokenizer<T> {
            return StringTokenizer(
                Regex(patterns.keys.map { "(${it})" }.joinToString("|")),
                patterns.values.toList()
            )
        }
    }
}

data class Token<T>(val position: IntRange, val content: T)

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
