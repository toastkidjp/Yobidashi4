package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import jp.toastkid.yobidashi4.presentation.editor.style.EditorStyle

class EditorTheme {

    private val plainText = EditorStyle(
        Regex("\\n-.*"),
        SpanStyle(Color(0xFF000B00)),
        SpanStyle(Color(0xFFF0F0F0))
    )

    private val patterns = listOf(
        EditorStyle(
            Regex("[0-9]*"),
            SpanStyle(Color(0xFF6897BB)),
            SpanStyle(Color(0xFF6897BB))
        ),
        EditorStyle(
            Regex("\\n#.*"),
            SpanStyle(Color(0xFF008800), fontWeight = FontWeight.Bold),
            SpanStyle(Color(0xFF00DD00), fontWeight = FontWeight.Bold)
        ),
        EditorStyle(
            Regex("\\n\\|.*"),
            SpanStyle(Color(0xFF8800CC)),
            SpanStyle(Color(0xFF68BB97))
        ),
        EditorStyle(
            Regex("\\n>.*"),
            SpanStyle(Color(0xFF7744AA)),
            SpanStyle(Color(0xFFCCAAFF))
        ),
        EditorStyle(
            Regex("\\n-.*"),
            SpanStyle(Color(0xFF777239)),
            SpanStyle(Color(0xFFCCAAFF))
        )
    )

    fun codeString(str: String, textColor: Color, darkTheme: Boolean) = buildAnnotatedString {
        withStyle(if (darkTheme) plainText.darkStyle else plainText.lightStyle) {
            append(str)

            patterns.forEach {
                addStyle(if (darkTheme) it.darkStyle else it.lightStyle, str, it.regex)
            }
        }
    }

    private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: Regex) {
        for (result in regexp.findAll(text)) {
            addStyle(style, result.range.first, result.range.last + 1)
        }
    }

    class Code(
        val simple: SpanStyle = SpanStyle(Color(0xFF000B00)),
        val header: SpanStyle = SpanStyle(Color(0xFF00DD00), fontWeight = FontWeight.Bold),
        val table: SpanStyle = SpanStyle(Color(0xFF68BB97)),
        val quote: SpanStyle = SpanStyle(Color(0xFFCCAAFF)),
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