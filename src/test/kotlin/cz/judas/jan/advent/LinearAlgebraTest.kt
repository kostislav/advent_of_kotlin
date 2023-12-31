package cz.judas.jan.advent

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LinearAlgebraTest {
    @Nested
    inner class FractionTest {
        @Test
        fun sumWorks() {
            assertThat(3L.toFraction() + 4L.toFraction(), equalTo(7L.toFraction()))
        }
    }
}