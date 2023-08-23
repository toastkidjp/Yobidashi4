package jp.toastkid.yobidashi4.presentation.text.code

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import jp.toastkid.yobidashi4.presentation.editor.EditorTheme

class CodeStringBuilder {

    operator fun invoke(str: String) = buildAnnotatedString {
        val theme = EditorTheme.get()
        withStyle(theme.code.simple) {
            append(str)
            addStyle(theme.code.punctuation, str, ":")
            addStyle(theme.code.punctuation, str, "=")
            addStyle(theme.code.punctuation, str, "\"")
            addStyle(theme.code.punctuation, str, "[")
            addStyle(theme.code.punctuation, str, "]")
            addStyle(theme.code.punctuation, str, "{")
            addStyle(theme.code.punctuation, str, "}")
            addStyle(theme.code.punctuation, str, "(")
            addStyle(theme.code.punctuation, str, ")")
            addStyle(theme.code.punctuation, str, ",")
            addStyle(theme.code.keyword, str, "fun ")
            addStyle(theme.code.keyword, str, "---")
            addStyle(theme.code.keyword, str, "val ")
            addStyle(theme.code.keyword, str, "var ")
            addStyle(theme.code.keyword, str, "private ")
            addStyle(theme.code.keyword, str, "internal ")
            addStyle(theme.code.keyword, str, "for ")
            addStyle(theme.code.keyword, str, "expect ")
            addStyle(theme.code.keyword, str, "actual ")
            addStyle(theme.code.keyword, str, "import ")
            addStyle(theme.code.keyword, str, "package ")
            addStyle(theme.code.value, str, "true")
            addStyle(theme.code.value, str, "false")
            addStyle(theme.code.value, str, Regex("[0-9]*"))
            addStyle(theme.code.header, str, Regex("\\n#.*"))
            addStyle(theme.code.table, str, Regex("\\n\\|.*"))
            addStyle(theme.code.quote, str, Regex("\\n>.*"))
            addStyle(theme.code.quote, str, Regex("\\n-.*"))
            addStyle(theme.code.annotation, str, Regex("^@[a-zA-Z_]*"))
            addStyle(theme.code.comment, str, Regex("^\\s*//.*"))
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