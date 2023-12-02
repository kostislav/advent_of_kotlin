package cz.judas.jan.advent

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

// Inspired by https://github.com/LittleLightCz/Rojo

annotation class Pattern(val pattern: String)

@Target(AnnotationTarget.TYPE)
annotation class SplitOn(val delimiter: String)

inline fun <reified T> String.parseAs(): T {
    return parse(this, typeOf<T>()) as T
}

fun parse(input: String, type: KType): Any {
    return when (val classifier = type.classifier) {
        String::class -> input
        Int::class -> input.toInt()
        is KClass<*> -> {
            if (classifier == List::class) {
                val delimiter = type.annotations.filterIsInstance(SplitOn::class.java).getOnlyElement().delimiter
                val itemType = type.arguments[0].type!!
                return input.split(delimiter).map { parse(it, itemType) }
            } else if (classifier.isSubclassOf(Enum::class)) {
                return classifier.java.enumConstants.associateBy { (it as Enum<*>).name }.getValue(input.uppercase())
            } else {
                parseRegex(
                    input,
                    classifier.annotations.filterIsInstance(Pattern::class.java).getOnlyElement().pattern,
                    classifier
                )
            }
        }

        else -> throw RuntimeException("Cannot parse into ${type}")
    }
}

private fun parseRegex(input: String, pattern: String, type: KClass<*>): Any {
    val match = Regex(pattern).matchEntire(input)
    if (match === null) {
        throw RuntimeException("Pattern ${pattern} did not match ${input}")
    } else {
        val constructor = type.primaryConstructor!!
        val arguments = constructor.parameters.zip(match.groupValues.subList(1))
            .map { (parameter, value) -> parse(value, parameter.type) }
        return constructor.call(*arguments.toTypedArray())
    }
}