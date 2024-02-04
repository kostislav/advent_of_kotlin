package cz.judas.jan.advent.year2015

import com.google.common.hash.Hashing
import cz.judas.jan.advent.Answer
import cz.judas.jan.advent.InputData
import cz.judas.jan.advent.mapSecond
import java.nio.charset.StandardCharsets

object Day4 {
    @Answer("282749")
    fun part1(input: InputData): Int {
        return findWithZeroes(input, 5)
    }

    @Answer("9962624")
    fun part2(input: InputData): Int {
        return findWithZeroes(input, 6)
    }

    private fun findWithZeroes(input: InputData, howMany: Int): Int {
        val mask = 0xffffff - ((1 shl 4 * (6 - howMany)) - 1)
        val secretKey = input.asString()
        val md5 = Hashing.md5()
        return positiveNumbers()
            .map { it to md5.hashString(secretKey + it, StandardCharsets.UTF_8).asBytes() }
            .mapSecond { (it[0].toInt() shl 16) + (it[1].toInt() shl 8) + it[2] }
            .first { it.second and mask == 0 }
            .first
    }

    private fun positiveNumbers(): Sequence<Int> {
        return generateSequence(0) { it + 1 }
    }
}
