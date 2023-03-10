package jp.toastkid.yobidashi4.presentation.editor.legacy.text

class BlockQuotation {

    /**
     * Line separator.
     */
    private val lineSeparator = "\n"

    /**
     * Invoke quotation function.
     *
     * @param text Nullable [CharSequence]
     */
    operator fun invoke(text: String?): String? {
        if (text.isNullOrEmpty()) {
            return text
        }
        val converted = text.trimEnd().split(lineSeparator)
                .asSequence()
                .map { "> $it" }
                .reduce { str1, str2 -> str1 + lineSeparator + str2 }
        return if (text.endsWith(lineSeparator)) converted.plus(lineSeparator) else converted
    }
}