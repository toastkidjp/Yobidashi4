package jp.toastkid.yobidashi4.infrastructure.service.chat

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class ChatResponseParser {

    operator fun invoke(inputStream: InputStream): String? {
        return BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use {
            val orElse = it.lines().filter { it.trim().startsWith("\"text\"") }.findFirst().orElse("")
            if (orElse.contains("\": \"").not()) {
                return null
            }

            return@use orElse.split("\": \"")[1].replace("\"$".toRegex(), "").replace("\\n", "\n")
        }
    }

}