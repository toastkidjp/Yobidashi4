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
import jp.toastkid.yobidashi4.infrastructure.service.chat.ChatStreamParser
import org.koin.core.annotation.Single
import org.slf4j.LoggerFactory

@Single
class ChatApi(private val apiKey: String) : ChatRepository {

    override fun request(content: String, streamLineConsumer: (String?) -> Unit) {
        val connection = openConnection() ?: return
        connection.setRequestProperty("Content-Type", "application/json")
        connection.requestMethod = "POST"
        connection.doInput = true
        connection.doOutput = true
        connection.readTimeout = 60_000
        connection.connect()

        BufferedWriter(OutputStreamWriter(connection.outputStream, StandardCharsets.UTF_8)).use {
            LoggerFactory.getLogger(javaClass).debug(content)
            it.write(content)
        }

        if (connection.responseCode != 200) {
            LoggerFactory.getLogger(javaClass).error("return error ${connection.responseCode} ${String(connection.errorStream.readAllBytes())}")
            return
        }

        val parser = ChatStreamParser()
        BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8)).use {
            var line = it.readLine()
            while (line != null) {
                val response = parser.invoke(line)
                if (response != null) {
                    streamLineConsumer(response)
                    LoggerFactory.getLogger(javaClass).debug(response)
                }

                line = it.readLine()
            }
        }
    }

    fun openConnection(): HttpURLConnection? =
        URL("https://generativelanguage.googleapis.com/v1/models/gemini-pro:streamGenerateContent?alt=sse&key=$apiKey").openConnection() as? HttpsURLConnection

}
