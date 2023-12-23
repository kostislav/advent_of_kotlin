package cz.judas.jan.advent

import org.apache.http.HttpMessage
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import java.io.FileInputStream
import java.io.Reader
import java.io.StringReader
import java.nio.charset.StandardCharsets
import kotlin.io.path.Path
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class InputData(private val source: () -> Reader) {
    fun lines(): List<String> {
        return source()
            .useLines { it.toList() }
    }

    fun asString(): String {
        return source().readText()
    }

//    TODO use more
    fun as2dArray(): TwoDimensionalArray<Char> {
        return TwoDimensionalArray.charsFromLines(lines())
    }

    companion object {
        fun fromString(contents: String): InputData {
            return InputData { StringReader(contents) }
        }
    }
}

class InputFetcher {
    fun get(year: Int, day: Int): InputData {
        val inputFile = Path("inputs/year${year}/day${day}")
        if (inputFile.notExists()) {
            val data = fetch(year, day)
            inputFile.writeText(data, StandardCharsets.UTF_8)
        }
        return InputData {
            FileInputStream(inputFile.toFile()).buffered().reader(StandardCharsets.UTF_8)
        }
    }

    private fun fetch(year: Int, day: Int): String {
        return AdventOfCodeApi().getPuzzleInput(year, day)
    }
}

class AdventOfCodeApi {
    private val httpClient = HttpClients.createDefault()

    fun getPuzzleInput(year: Int, day: Int): String {
        val request = HttpGet("https://adventofcode.com/${year}/day/${day}/input").apply {
            addHeaders()
        }

        return httpClient.execute(request).use { it.entity.content.reader().readText().trimEnd('\n') }
    }

    fun submitAnswer(year: Int, day: Int, part: Int, answer: Any) {
        val request = HttpPost("https://adventofcode.com/${year}/day/${day}/answer").apply {
            addHeaders()
            entity = UrlEncodedFormEntity(
                listOf(
                    BasicNameValuePair("level", part.toString()),
                    BasicNameValuePair("answer", answer.toString()),
                )
            )
        }

        httpClient.execute(request).close()
    }

    private fun HttpMessage.addHeaders() {
        setHeader("Cookie", "session=${getSession()}")
        setHeader("User-Agent", "https://github.com/kostislav/advent_of_kotlin by snugar.i@gmail.com")
    }

    private fun getSession() = Path(".session").readText()
}