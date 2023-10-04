package jp.toastkid.yobidashi4.presentation.editor.finder

data class FindOrder(
        val target: String,
        val replace: String,
        val upper: Boolean = false,
        val invokeReplace: Boolean = false,
        val caseSensitive: Boolean = true
) {
        companion object {
                val EMPTY = FindOrder("", "")
        }
}