package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import jp.toastkid.yobidashi4.presentation.editor.style.EditorStyle

class EditorTheme {

    private val patterns = listOf(
        EditorStyle(
            Regex("[0-9]*"),
            SpanStyle(Color(0xFF6897BB)),
            SpanStyle(Color(0xFFA8B7EE))
        ),
        EditorStyle(
            Regex("^#.*"),
            SpanStyle(Color(0xFF008800), fontWeight = FontWeight.Bold),
            SpanStyle(Color(0xFF00DD00), fontWeight = FontWeight.Bold)
        ),
        EditorStyle(
            Regex("\\n#.*"),
            SpanStyle(Color(0xFF008800), fontWeight = FontWeight.Bold),
            SpanStyle(Color(0xFF00DD00), fontWeight = FontWeight.Bold)
        ),
        EditorStyle(
            Regex("\\n\\|.*"),
            SpanStyle(Color(0xFF8800CC)),
            SpanStyle(Color(0xFF86EEC7))
        ),
        EditorStyle(
            Regex("\\n>.*"),
            SpanStyle(Color(0xFF7744AA)),
            SpanStyle(Color(0xFFCCAAFF))
        ),
        EditorStyle(
            Regex("\\n-.*"),
            SpanStyle(Color(0xFF666239)),
            SpanStyle(Color(0xFFDDBBFF))
        )
    )

    fun codeString(str: AnnotatedString, darkTheme: Boolean) = AnnotatedString.Builder(str).apply {
        patterns.forEach {
            for (result in it.regex.findAll(str)) {
                addStyle(if (darkTheme) it.darkStyle else it.lightStyle, result.range.first, result.range.last + 1)
            }
        }
    }.toAnnotatedString()

    private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: Regex) {
        for (result in regexp.findAll(text)) {
            addStyle(style, result.range.first, result.range.last + 1)
        }
    }

}