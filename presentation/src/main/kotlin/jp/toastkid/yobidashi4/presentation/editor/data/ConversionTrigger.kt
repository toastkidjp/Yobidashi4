package jp.toastkid.yobidashi4.presentation.editor.data

data class ConversionTrigger(
    val lineCount: Int,
    val lineStarts: String,
    val inComposition: Boolean
)
