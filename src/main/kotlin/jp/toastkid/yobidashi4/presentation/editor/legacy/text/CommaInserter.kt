package jp.toastkid.yobidashi4.presentation.editor.legacy.text

class CommaInserter {

    operator fun invoke(text: String?): String? {
        val toCharArray = text?.toCharArray() ?: return null
        return with(StringBuilder()) {
            (toCharArray.indices).forEach { index ->
                if (toCharArray.size > 3 && isNotEmpty() && index % 3 == (toCharArray.size % 3)) {
                    append(",")
                }
                append(toCharArray[index])
            }
            toString()
        }
    }

}