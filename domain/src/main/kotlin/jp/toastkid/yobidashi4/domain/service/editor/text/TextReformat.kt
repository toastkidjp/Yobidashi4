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
                str.fold(0) { count, char ->
                    if (char.isWhitespace()) {
                        return@fold count + 1
                    }

                    return@fold count
                }
            }
            .min()
    }

}
