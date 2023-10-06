package jp.toastkid.yobidashi4.presentation.editor.markdown.text

class CommaInserter {

    operator fun invoke(text: String?): String? {
        val toCharArray = text?.toCharArray() ?: return null
        return with(StringBuilder()) {
            (toCharArray.indices).forEach { index ->
                if (toCharArray.size > NUMBER_OF_DIGITS && isNotEmpty() && index % NUMBER_OF_DIGITS == (toCharArray.size % NUMBER_OF_DIGITS)) {
                    append(",")
                }
                append(toCharArray[index])
            }
            toString()
        }
    }

}

private const val NUMBER_OF_DIGITS = 3
