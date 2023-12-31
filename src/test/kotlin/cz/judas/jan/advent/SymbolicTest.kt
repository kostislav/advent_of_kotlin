package cz.judas.jan.advent

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SymbolicTest {
    @Nested
    inner class ProductTest {
        @Test
        fun producesCorrectCoefficientsForConstantAndVariable() {
            val variable = Variable("x")
            val product = Product(listOf(Constant(6), variable))

            val coefficients = product.coefficients()

            assertThat(coefficients, equalTo(mapOf(multiSetOf(variable) to 6.0)))
        }

        @Test
        fun producesCorrectCoefficientsForTwoVariables() {
            val variable = Variable("x")
            val product = Product(listOf(variable, variable))

            val coefficients = product.coefficients()

            assertThat(coefficients, equalTo(mapOf(multiSetOf(variable, variable) to 1.0)))
        }
    }
}