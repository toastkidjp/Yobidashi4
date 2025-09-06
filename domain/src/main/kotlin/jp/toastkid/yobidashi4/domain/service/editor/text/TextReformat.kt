package jp.toastkid.yobidashi4.domain.service.editor.text

import kotlinx.serialization.json.Json

class TextReformat {

    operator fun invoke(input: String): String {
        if (input.isEmpty()) {
            return input
        }

        val trimmed = input.trim()
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            val json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
            val jsonElement = Json.parseToJsonElement(trimmed)
            return json.encodeToString(jsonElement)
        }

        val minIndent = input.split("\n").filter { it.isNotEmpty() }.map { str ->
            var count = 0
            for (char in str) {
                if (char.isWhitespace()) {
                    count++
                    continue
                }

                return@map count
            }
            return@map count
        }.min()

        val whitespaces = " ".repeat(minIndent)
        return input.split("\n").joinToString("\n") { it.replaceFirst(whitespaces, "") }
    }

}
