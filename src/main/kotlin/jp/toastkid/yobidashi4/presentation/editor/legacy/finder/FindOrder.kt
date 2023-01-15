package jp.toastkid.yobidashi4.presentation.editor.legacy.finder

data class FindOrder(
        val target: String,
        val replace: String,
        val upper: Boolean = false,
        val invokeReplace: Boolean = false,
        val caseSensitive: Boolean = true
)