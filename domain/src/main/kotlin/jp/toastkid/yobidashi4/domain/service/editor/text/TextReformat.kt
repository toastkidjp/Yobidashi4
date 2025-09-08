package jp.toastkid.yobidashi4.domain.service.editor.text

class TextReformat {

    operator fun invoke(input: String): String {
        if (input.isEmpty()) {
            return input
        }

        val minIndent = calculateMinimumIndent(input)

        val whitespaces = " ".repeat(minIndent)
        return input.split("\n").joinToString("\n") { it.replaceFirst(whitespaces, "") }
    }

    private fun calculateMinimumIndent(input: String): Int {
        return input.split("\n")
            .filter(CharSequence::isNotEmpty)
            .map { str ->
                var count = 0
                for (char in str) {
                    if (char.isWhitespace()) {
                        count++
                        continue
                    }

                    return@map count
                }
                return@map count
            }
            .min()
    }

}
