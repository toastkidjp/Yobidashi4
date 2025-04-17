package jp.toastkid.yobidashi4.presentation.editor.markdown.text

class TrimmingService {

    operator fun invoke(text: String?): String? {
        if (text.isNullOrEmpty()) {
            return text
        }

        val converted = text.trimEnd().split(LINE_SEPARATOR)
                .asSequence()
                .map(String::trim)
                .reduce { str1, str2 -> str1 + LINE_SEPARATOR + str2 }
        return if (text.endsWith(LINE_SEPARATOR)) converted.plus(LINE_SEPARATOR) else converted
    }

    companion object {

        /**
         * If you use this app in other environment, you should use appropriate line separator.
         */
        private const val LINE_SEPARATOR = "\n"

    }

}