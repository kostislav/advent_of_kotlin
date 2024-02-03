#!/bin/bash

YEAR="${1}"
DAY="${2}"

SOURCE_FILE="src/main/kotlin/cz/judas/jan/advent/year${YEAR}/Day${DAY}.kt"
TEST_FILE="src/test/kotlin/cz/judas/jan/advent/year${YEAR}/Day${DAY}Test.kt"

cat << EOF > "${SOURCE_FILE}"
package cz.judas.jan.advent.year${YEAR}

import cz.judas.jan.advent.InputData

object Day${DAY} {
    fun part1(input: InputData): Int {
        return 0
    }

    fun part2(input: InputData): Int {
        return 0
    }
}
EOF

cat << EOF > "${TEST_FILE}"
package cz.judas.jan.advent.year${YEAR}

import cz.judas.jan.advent.InputData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class Day${DAY}Test {
    private val input = InputData.fromString(
        """

        """.trimIndent()
    )

    @Test
    fun part1() {
        assertThat(Day${DAY}.part1(input), equalTo(10))
    }

    @Test
    fun part2() {
        assertThat(Day${DAY}.part2(input), equalTo(10))
    }
}
EOF

git add "${SOURCE_FILE}"
git add "${TEST_FILE}"