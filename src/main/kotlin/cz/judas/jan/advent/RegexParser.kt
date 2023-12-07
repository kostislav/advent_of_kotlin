package cz.judas.jan.advent

import org.intellij.lang.annotations.Language
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

// Inspired by https://github.com/LittleLightCz/Rojo

// multiline parameter with default value does not work:
// https://youtrack.jetbrains.com/issue/KT-39369/KotlinReflectionInternalError-Method-is-not-supported-for-default-value-in-type-annotation
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class Pattern(@Language("RegExp") val pattern: String)

@Target(AnnotationTarget.TYPE)
annotation class SplitOn(vararg val delimiters: String)

@Target(AnnotationTarget.TYPE)
annotation class SplitOnPattern(@Language("RegExp") val delimiterPattern: String)

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
        Long::class -> LongParser
        is KClass<*> -> {
            if (classifier == List::class) {
                val itemType = type.arguments[0].type!!
                val splitOnAnnotation = type.getAnnotation(SplitOn::class)
                if (splitOnAnnotation === null) {
                    val splitOnPatternAnnotation = type.getAnnotation(SplitOnPattern::class)
                    if (splitOnPatternAnnotation === null) {
                        val patternAnnotation = itemType.getAnnotation(Pattern::class)
                        if (patternAnnotation === null) {
                            if (itemType.classifier == Char::class) {
                                return CharListParser
                            } else {
                                throw RuntimeException("Cannot parse into ${type}")
                            }
                        } else {
                            return PatternListParser(
                                buildRegex(patternAnnotation),
                                buildParser(itemType)
                            )
                        }
                    } else {
                        return PatternSplittingListParser(
                            Regex(splitOnPatternAnnotation.delimiterPattern),
                            buildParser(itemType)
                        )
                    }
                } else {
                    val delimiters = splitOnAnnotation.delimiters
                    return SplittingListParser(delimiters, buildParser(itemType))
                }
            } else if (classifier.isSubclassOf(Enum::class)) {
                val values = classifier.java.enumConstants.associateBy { (it as Enum<*>).name }
                return EnumParser(values)
            } else {
                val constructor = classifier.primaryConstructor!!
                return PatterClassParser(
                    buildRegex(classifier.getAnnotation(Pattern::class)!!),
                    constructor,
                    constructor.parameters.map { buildParser(it.type) }
                )
            }
        }

        else -> throw RuntimeException("Cannot parse into ${type}")
    }

}

private fun buildRegex(pattern: Pattern): Regex {
    return Regex(pattern.pattern, setOf(RegexOption.DOT_MATCHES_ALL))
}

private fun <T: Any> KAnnotatedElement.getAnnotation(annotationType: KClass<T>): T? {
    val found = annotations.filterIsInstance(annotationType.java)
    return when (found.size) {
        0 -> null
        1 -> found[0]
        else -> throw RuntimeException("Multiple annotations of type ${annotationType} found")
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

object LongParser : Parser<Long> {
    override fun parse(input: String): Long {
        return input.toLong()
    }
}

object CharListParser : Parser<List<Char>> {
    override fun parse(input: String): List<Char> {
        return input.characters()
    }
}

class EnumParser<T>(
    private val options: Map<String, T>
) : Parser<T> {
    override fun parse(input: String): T {
        return options.getValue(input.uppercase())
    }
}

class SplittingListParser<T>(
    private val delimiters: Array<out String>,
    private val itemParser: Parser<T>
) : Parser<List<T>> {
    override fun parse(input: String): List<T> {
        return input.split(*delimiters).map(itemParser::parse)
    }
}

class PatternSplittingListParser<T>(
    private val splitPattern: Regex,
    private val itemParser: Parser<T>
) : Parser<List<T>> {
    override fun parse(input: String): List<T> {
        return input.split(splitPattern).filterNot { it.isEmpty() }.map(itemParser::parse)
    }
}

class PatternListParser<T>(
    private val pattern: Regex,
    private val itemParser: Parser<T>
) : Parser<List<T>> {
    override fun parse(input: String): List<T> {
        return pattern.findAll(input).toList()
            .map { matchResult ->
                val groups = matchResult.groups
                if (groups.size > 1) {
                    groups[1]!!.value
                } else {
                    groups[0]!!.value
                }
            }
            .map(itemParser::parse)
    }
}