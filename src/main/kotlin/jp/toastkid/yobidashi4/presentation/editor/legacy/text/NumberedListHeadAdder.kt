package jp.toastkid.yobidashi4.presentation.editor.legacy.text

class NumberedListHeadAdder {

    /**
     * @param text Nullable [CharSequence]
     */
    operator fun invoke(text: String?): String? {
        if (text.isNullOrEmpty()) {
            return text
        }

        return text.trimEnd().split("\n")
                .mapIndexed { index, s -> "${index + 1}. $s" }
                .reduceRight { s, acc -> "$s\n$acc" } + "\n"
    }

}