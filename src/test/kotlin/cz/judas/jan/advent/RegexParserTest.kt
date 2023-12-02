package cz.judas.jan.advent

import org.junit.jupiter.api.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

class RegexParserTest {
    @Test
    fun parsesSimpleDataClass() {
        val input = "aa is bb"

        val parsed = parserFor<Simple>().parse(input)

        assertThat(parsed, equalTo(Simple("aa", "bb")))
    }

    @Test
    fun parsesIntField() {
        val input = "cc is 654"

        val parsed = parserFor<WithInt>().parse(input)

        assertThat(parsed, equalTo(WithInt("cc", 654)))
    }

    @Test
    fun parsesListField() {
        val input = "dd is a list with 1, 2, 3, 4"

        val parsed = parserFor<WithList>().parse(input)

        assertThat(parsed, equalTo(WithList("dd", listOf(1, 2, 3, 4))))
    }

    @Test
    fun parsesEnumField() {
        val input = "ee is a one"

        val parsed = parserFor<WithEnum>().parse(input)

        assertThat(parsed, equalTo(WithEnum("ee", ExampleEnum.ONE)))
    }

    @Pattern("([a-z]+) is ([a-z]+)")
    data class Simple(val first: String, val second: String)

    @Pattern("([a-z]+) is (\\d+)")
    data class WithInt(val first: String, val second: Int)

    @Pattern("([a-z]+) is a list with (.+)")
    data class WithList(val first: String, val second: @SplitOn(", ") List<Int>)

    @Pattern("([a-z]+) is a ([a-z]+)")
    data class WithEnum(val first: String, val second: ExampleEnum)

    @Suppress("unused")
    enum class ExampleEnum {
        ONE, TWO
    }
}