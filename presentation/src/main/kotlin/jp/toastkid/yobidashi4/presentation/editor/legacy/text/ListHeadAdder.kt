package jp.toastkid.yobidashi4.presentation.editor.legacy.text

class ListHeadAdder {

    /**
     * @param text Nullable [CharSequence]
     * @param head
     */
    operator fun invoke(text: String?, head: String): String? {
        if (text.isNullOrEmpty()) {
            return text
        }

        val putEndLineBreak = if (text.endsWith("\n")) "\n" else ""

        return head + " " + text.trimEnd().replace("\n", "\n$head ") + putEndLineBreak
    }

}