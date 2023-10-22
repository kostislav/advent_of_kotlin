package cz.judas.jan.advent

import java.io.InputStream
import java.nio.charset.StandardCharsets
import kotlin.experimental.ExperimentalTypeInference

fun <I, O> Iterator<I>.map(transformation: (I) -> O): Iterator<O> {
    val original = this

    return object : Iterator<O> {
        override fun hasNext(): Boolean = original.hasNext()

        override fun next(): O = transformation(original.next())
    }
}

fun Iterator<Int>.sum() : Int {
    return asSequence().sum()
}

fun <T : Comparable<T>> Iterator<T>.max() : T {
    return asSequence().maxOrNull()!!
}

@OptIn(ExperimentalTypeInference::class)
fun <T> generatedIterator(@BuilderInference block: suspend SequenceScope<T>.() -> Unit): Iterator<T> {
    val s = Sequence { iterator(block) }
    return s.iterator()
}

fun <T> Iterator<T>.splitOn(predicate: (T) -> Boolean): Iterator<Iterator<T>> {
    val original = this

    return generatedIterator {
        while (original.hasNext()) {
            yield(generatedIterator inner@{
                while (original.hasNext()) {
                    val next = original.next()
                    if (predicate(next)) {
                        return@inner
                    } else {
                        yield(next)
                    }
                }
            })
        }
    }
}

class InputData(private val source: () -> InputStream) {
    fun <T> transformLines(transformation: (Iterator<String>) -> T): T {
        return source()
            .reader(StandardCharsets.UTF_8)
            .useLines { transformation(it.iterator()) }
    }

    companion object {
        fun forDay(day: Int): InputData {
            val resource = InputData::class.java.getResource("day${day}")!!
            return InputData { resource.openStream() }
        }
    }
}