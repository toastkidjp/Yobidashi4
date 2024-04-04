package jp.toastkid.yobidashi4.infrastructure.repository.chat

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection
import jp.toastkid.yobidashi4.domain.repository.chat.ChatRepository
import org.koin.core.annotation.Single
import org.slf4j.LoggerFactory

@Single
class ChatApi(private val apiKey: String) : ChatRepository {

    override fun request(content: String): String? {
        val connection = openConnection() ?: return null
        connection.setRequestProperty("Content-Type", "application/json")
        connection.requestMethod = "POST"
        connection.doInput = true
        connection.doOutput = true
        connection.connect()

        BufferedWriter(OutputStreamWriter(connection.outputStream, StandardCharsets.UTF_8)).use {
            LoggerFactory.getLogger(javaClass).debug(content)
            it.write(content)
        }

        if (connection.responseCode != 200) {
            LoggerFactory.getLogger(javaClass).error("return error ${connection.responseCode} ${String(connection.errorStream.readAllBytes())}")
            return null
        }

        return BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8)).use {
            val orElse = it.lines().filter { it.trim().startsWith("\"text\"") }.findFirst().orElse("")
            if (orElse.contains("\": \"").not()) {
                return null
            }

            return@use orElse.split("\": \"")[1].replace("\"$".toRegex(), "").replace("\\n", "\n")
        }
    }

    fun openConnection(): HttpURLConnection? =
        URL("https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key=$apiKey").openConnection() as? HttpsURLConnection

}