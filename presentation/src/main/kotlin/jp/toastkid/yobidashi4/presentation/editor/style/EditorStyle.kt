package jp.toastkid.yobidashi4.presentation.editor.style

import androidx.compose.ui.text.SpanStyle

data class EditorStyle(
    val regex: Regex,
    val lightStyle: SpanStyle,
    val darkStyle: SpanStyle
)