package jp.toastkid.yobidashi4.presentation.text.code

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

class CodeStringBuilder {

    private val simple: SpanStyle = SpanStyle(Color(0xFF000B00))

    private val header: SpanStyle = SpanStyle(Color(0xFF00DD00), fontWeight = FontWeight.Bold)

    private val table: SpanStyle = SpanStyle(Color(0xFF68BB97))

    private val quote: SpanStyle = SpanStyle(Color(0xFFCCAAFF))

    private val value: SpanStyle = SpanStyle(Color(0xFF6897BB))

    private val keyword: SpanStyle = SpanStyle(Color(0xFFCC7832))

    private val punctuation: SpanStyle = SpanStyle(Color(0xFFA1C17E))

    private val annotation: SpanStyle = SpanStyle(Color(0xFFBBB529))

    private val comment: SpanStyle = SpanStyle(Color(0xFF808080))
    
    operator fun invoke(str: String) = buildAnnotatedString {
        withStyle(simple) {
            append(str)
            addStyle(punctuation, str, ":")
            addStyle(punctuation, str, "=")
            addStyle(punctuation, str, "\"")
            addStyle(punctuation, str, "[")
            addStyle(punctuation, str, "]")
            addStyle(punctuation, str, "{")
            addStyle(punctuation, str, "}")
            addStyle(punctuation, str, "(")
            addStyle(punctuation, str, ")")
            addStyle(punctuation, str, ",")
            addStyle(keyword, str, "fun ")
            addStyle(keyword, str, "---")
            addStyle(keyword, str, "val ")
            addStyle(keyword, str, "var ")
            addStyle(keyword, str, "private ")
            addStyle(keyword, str, "internal ")
            addStyle(keyword, str, "for ")
            addStyle(keyword, str, "expect ")
            addStyle(keyword, str, "actual ")
            addStyle(keyword, str, "import ")
            addStyle(keyword, str, "package ")
            addStyle(value, str, "true")
            addStyle(value, str, "false")
            addStyle(value, str, Regex("[0-9]*"))
            addStyle(header, str, Regex("\\n#.*"))
            addStyle(table, str, Regex("\\n\\|.*"))
            addStyle(quote, str, Regex("\\n>.*"))
            addStyle(quote, str, Regex("\\n-.*"))
            addStyle(annotation, str, Regex("^@[a-zA-Z_]*"))
            addStyle(comment, str, Regex("^\\s*//.*"))
        }
    }

    private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: String) {
        addStyle(style, text, Regex.fromLiteral(regexp))
    }

    private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: Regex) {
        for (result in regexp.findAll(text)) {
            addStyle(style, result.range.first, result.range.last + 1)
        }
    }

}