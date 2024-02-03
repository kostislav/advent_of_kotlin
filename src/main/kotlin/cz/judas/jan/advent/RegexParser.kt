package cz.judas.jan.advent

import org.intellij.lang.annotations.Language
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

// Inspired by https://github.com/LittleLightCz/Rojo

// multiline parameter with default value does not work:
// https://youtrack.jetbrains.com/issue/KT-39369/KotlinReflectionInternalError-Method-is-not-supported-for-default-value-in-type-annotation
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class Pattern(@Language("RegExp") val pattern: String)

@Target(AnnotationTarget.TYPE)
annotation class SplitOn(vararg val delimiters: String)

@Target(AnnotationTarget.TYPE)
annotation class SplitOnPattern(@Language("RegExp") val delimiterPattern: String)

interface ParsedFromString {
    val stringValue: String
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> parserFor(): Parser<T> {
    return buildParser(typeOf<T>()) as Parser<T>
}

fun <T> parserUsing(function: KCallable<T>): Parser<T> {
    return PatterFunctionParser(
        Regex(function.getAnnotation(Pattern::class)!!.pattern),
        function,
        function.parameters.map { buildParser(it.type) }
    )
}

inline fun <T, reified U1, reified U2> lambdaParser(@Language("RegExp") pattern: String, noinline function: (U1, U2) -> T): Parser<T> {
    val wrapper = Function2Wrapper(function)
    return PatterFunctionParser(
        Regex(pattern),
        wrapper::call,
        listOf(
            buildParser(typeOf<U1>()),
            buildParser(typeOf<U2>())
        )
    )
}

inline fun <T, reified U1, reified U2, reified U3> lambdaParser(@Language("RegExp") pattern: String, noinline function: (U1, U2, U3) -> T): Parser<T> {
    val wrapper = Function3Wrapper(function)
    return PatterFunctionParser(
        Regex(pattern),
        wrapper::call,
        listOf(
            buildParser(typeOf<U1>()),
            buildParser(typeOf<U2>()),
            buildParser(typeOf<U3>()),
        )
    )
}

inline fun <T, reified U1, reified U2> List<String>.mapParsing(@Language("RegExp") pattern: String, noinline function: (U1, U2) -> T): List<T> {
    val parser = lambdaParser(pattern, function)
    return map(parser::parse)
}

inline fun <T, reified U1, reified U2, reified U3> List<String>.mapParsing(@Language("RegExp") pattern: String, noinline function: (U1, U2, U3) -> T): List<T> {
    val parser = lambdaParser(pattern, function)
    return map(parser::parse)
}

fun <T> String.parse(parser: Parser<T>): T {
    return parser.parse(this)
}

fun <T> customParser(parsingFunction: (String) -> T): Parser<T> {
    return CustomLambdaParser(parsingFunction)
}

fun buildParser(type: KType): Parser<Any> {
    return when (val classifier = type.classifier) {
        String::class -> StringParser
        Int::class -> IntParser
        Long::class -> LongParser
        Char::class -> CharParser
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
                val values = if (classifier.isSubclassOf(ParsedFromString::class)) {
                    classifier.java.enumConstants.associateBy { (it as ParsedFromString).stringValue }
                } else {
                    classifier.java.enumConstants.associateBy { (it as Enum<*>).name }
                }
                return EnumParser(values)
            } else if (classifier.isSealed) {
                return SealedClassParser(
                    classifier.sealedSubclasses.map(::parserForClass)
                )
            } else {
                return parserForClass(classifier)
            }
        }

        else -> throw RuntimeException("Cannot parse into ${type}")
    }
}

private fun parserForClass(classifier: KClass<*>): PatterFunctionParser<Any> {
    val constructor = classifier.primaryConstructor!!
    return PatterFunctionParser(
        buildRegex(classifier.getAnnotation(Pattern::class)!!),
        constructor,
        constructor.parameters.map { buildParser(it.type) }
    )
}

private fun buildRegex(pattern: Pattern): Regex {
    return Regex(pattern.pattern, setOf(RegexOption.DOT_MATCHES_ALL))
}

private fun <T : Any> KAnnotatedElement.getAnnotation(annotationType: KClass<T>): T? {
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

class PatterFunctionParser<T>(
    private val pattern: Regex,
    private val constructor: KCallable<T>,
    private val parameterParsers: List<Parser<Any>>,
) : Parser<T> {
    override fun parse(input: String): T {
        val result = parseOrNull(input)
        if (result === null) {
            throw RuntimeException("Pattern ${pattern.pattern} did not match ${input}")
        } else {
            return result
        }
    }

    fun parseOrNull(input: String): T? {
        val match = pattern.matchEntire(input)
        return if (match === null) {
            null
        } else {
            val arguments = parameterParsers.zip(match.groupValues.subList(1))
                .map { (parser, value) -> parser.parse(value) }
            constructor.call(*arguments.toTypedArray())
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

object CharParser : Parser<Char> {
    override fun parse(input: String): Char {
        if (input.length > 1) {
            throw RuntimeException("Cannot convert multi-character string to char")
        } else {
            return input[0]
        }
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

class CustomLambdaParser<T>(
    private val parsingFunction: (String) -> T
) : Parser<T> {
    override fun parse(input: String): T {
        return parsingFunction(input)
    }
}

class Function2Wrapper<T1, T2, U>(private val function: (T1, T2) -> U) {
    fun call(param1: T1, param2: T2): U {
        return function(param1, param2)
    }
}

class Function3Wrapper<T1, T2, T3, U>(private val function: (T1, T2, T3) -> U) {
    fun call(param1: T1, param2: T2, param3: T3): U {
        return function(param1, param2, param3)
    }
}

class SealedClassParser<T>(
    private val options: List<PatterFunctionParser<T>>
) : Parser<T> {
    override fun parse(input: String): T {
        return options.firstNotNullOf { it.parseOrNull(input) }
    }
}