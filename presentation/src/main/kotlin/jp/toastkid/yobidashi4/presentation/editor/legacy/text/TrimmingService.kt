package jp.toastkid.yobidashi4.presentation.editor.legacy.text

class TrimmingService {

    operator fun invoke(text: String?): String? {
        if (text.isNullOrEmpty()) {
            return text
        }

        val converted = text.trimEnd().split(lineSeparator)
                .asSequence()
                .map { it.trim() }
                .reduce { str1, str2 -> str1 + lineSeparator + str2 }
        return if (text.endsWith(lineSeparator)) converted.plus(lineSeparator) else converted
    }

    companion object {

        /**
         * If you use this app in other environment, you should use appropriate line separator.
         */
        private const val lineSeparator = "\n"

    }

}