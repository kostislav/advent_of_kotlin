package cz.judas.jan.advent

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

// Inspired by https://github.com/LittleLightCz/Rojo

annotation class Pattern(val pattern: String)

@Target(AnnotationTarget.TYPE)
annotation class SplitOn(val delimiter: String)

@Suppress("UNCHECKED_CAST")
inline fun <reified T> parserFor(): Parser<T> {
    return buildParser(typeOf<T>()) as Parser<T>
}

fun <T> String.parse(parser: Parser<T>): T {
    return parser.parse(this)
}

fun buildParser(type: KType): Parser<Any> {
    return when (val classifier = type.classifier) {
        String::class -> StringParser
        Int::class -> IntParser
        is KClass<*> -> {
            if (classifier == List::class) {
                val delimiter = type.annotations.filterIsInstance(SplitOn::class.java).getOnlyElement().delimiter
                val itemType = type.arguments[0].type!!
                return ListParser(delimiter, buildParser(itemType))
            } else if (classifier.isSubclassOf(Enum::class)) {
                val values = classifier.java.enumConstants.associateBy { (it as Enum<*>).name }
                return EnumParser(values)
            } else {
                val constructor = classifier.primaryConstructor!!
                return PatterClassParser(
                    Regex(classifier.annotations.filterIsInstance(Pattern::class.java).getOnlyElement().pattern),
                    constructor,
                    constructor.parameters.map { buildParser(it.type) }
                )
            }
        }

        else -> throw RuntimeException("Cannot parse into ${type}")
    }

}

interface Parser<out T> {
    fun parse(input: String): T
}

class PatterClassParser<T>(
    private val pattern: Regex,
    private val constructor: KFunction<T>,
    private val parameterParsers: List<Parser<Any>>,
) : Parser<T> {
    override fun parse(input: String): T {
        val match = pattern.matchEntire(input)
        if (match === null) {
            throw RuntimeException("Pattern ${pattern.pattern} did not match ${input}")
        } else {
            val arguments = parameterParsers.zip(match.groupValues.subList(1))
                .map { (parser, value) -> parser.parse(value) }
            return constructor.call(*arguments.toTypedArray())
        }
    }
}

object StringParser : Parser<String> {
    override fun parse(input: String): String {
        return input
    }

}

object IntParser : Parser<Int> {
    override fun parse(input: String): Int {
        return input.toInt()
    }
}

class EnumParser<T>(
    private val options: Map<String, T>
) : Parser<T> {
    override fun parse(input: String): T {
        return options.getValue(input.uppercase())
    }
}

class ListParser<T>(
    private val delimiter: String,
    private val itemParser: Parser<T>
) : Parser<List<T>> {
    override fun parse(input: String): List<T> {
        return input.split(delimiter).map(itemParser::parse)
    }
}