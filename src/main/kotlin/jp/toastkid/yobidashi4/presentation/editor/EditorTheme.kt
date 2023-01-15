package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight

class EditorTheme {

    class Code(
        val simple: SpanStyle = SpanStyle(Color(0xFF000B00)),
        val header: SpanStyle = SpanStyle(Color(0xFF008800), fontWeight = FontWeight.Bold),
        val table: SpanStyle = SpanStyle(Color(0xFF8800CC)),
        val quote: SpanStyle = SpanStyle(Color(0xFF7744AA)),
        val value: SpanStyle = SpanStyle(Color(0xFF6897BB)),
        val keyword: SpanStyle = SpanStyle(Color(0xFFCC7832)),
        val punctuation: SpanStyle = SpanStyle(Color(0xFFA1C17E)),
        val annotation: SpanStyle = SpanStyle(Color(0xFFBBB529)),
        val comment: SpanStyle = SpanStyle(Color(0xFF808080))
    )

    val code = Code()

    companion object {

        private val instance = EditorTheme()

        fun get() = instance

    }

}