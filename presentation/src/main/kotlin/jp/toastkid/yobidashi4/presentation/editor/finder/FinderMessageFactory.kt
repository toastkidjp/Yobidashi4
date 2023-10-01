package jp.toastkid.yobidashi4.presentation.editor.finder

class FinderMessageFactory {

    operator fun invoke(targetText: String, foundCount: Int): String {
        return with(StringBuilder()) {
            if (targetText.isBlank()) {
                return ""
            }

            append("\"${targetText}\" was ")
            if (foundCount <= 0 || targetText.isBlank()) {
                append("not ")
            }
            append("found.")

            if (foundCount > 0) {
                append(" ")
                append(foundCount)
            }

            toString()
        }
    }

}