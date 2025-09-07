package jp.toastkid.yobidashi4.domain.service.editor.text

class TextReformat(private val jsonPrettyPrint: JsonPrettyPrint = JsonPrettyPrint()) {

    operator fun invoke(input: String): String {
        if (input.isEmpty()) {
            return input
        }

        val trimmed = input.trim()
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return jsonPrettyPrint(trimmed)
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
