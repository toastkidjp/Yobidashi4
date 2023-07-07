package jp.toastkid.yobidashi4.presentation.editor.legacy.text

class CommaInserter {

    private val NUMBER_OF_DIGITS = 3

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