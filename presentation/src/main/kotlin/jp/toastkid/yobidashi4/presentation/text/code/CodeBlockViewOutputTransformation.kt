package jp.toastkid.yobidashi4.presentation.text.code

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import java.util.regex.Pattern

class CodeBlockViewOutputTransformation : OutputTransformation {

    private val simple: SpanStyle = SpanStyle()

    private val header: SpanStyle = SpanStyle(Color(0xFF00DD00), fontWeight = FontWeight.Bold)

    private val table: SpanStyle = SpanStyle(Color(0xFF68BB97))

    private val quote: SpanStyle = SpanStyle(Color(0xFFCCAAFF))

    private val value: SpanStyle = SpanStyle(Color(0xFF6897BB))

    private val keyword: SpanStyle = SpanStyle(Color(0xFFCC7832))

    private val punctuation: SpanStyle = SpanStyle(Color(0xFFA1C17E))

    private val annotation: SpanStyle = SpanStyle(Color(0xFFBBB529))

    private val comment: SpanStyle = SpanStyle(Color(0xFF808080))

    private val punctuationPattern = Pattern.compile("[:=\"\\[\\]\\{\\}\\(\\),]")

    private val keywordPattern = Pattern.compile("\\b(fun|val|var|private|internal|for|expect|actual|import|package|static|object) ")

    private val valuePattern = Pattern.compile("(true|false)")

    private val digitPattern = Pattern.compile("[0-9]*")

    override fun TextFieldBuffer.transformOutput() {
        applyPattern(punctuationPattern, punctuation)
        applyPattern(keywordPattern, keyword)
        applyPattern(valuePattern, value)
        applyPattern(digitPattern, value)
    }

    private fun TextFieldBuffer.applyPattern(pattern: Pattern, style: SpanStyle) {
        val matcher = pattern.matcher(asCharSequence())
        while (matcher.find()) {
            addStyle(style, matcher.start(), matcher.end())
        }
    }

}