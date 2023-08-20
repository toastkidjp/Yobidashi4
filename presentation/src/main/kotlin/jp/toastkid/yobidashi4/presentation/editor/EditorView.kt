package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.withStyle
import kotlin.text.Regex.Companion.fromLiteral
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope

@Composable
private fun LineContent(content: Editor.Content, modifier: Modifier) =
    BasicTextField(
        value = content.value.value,
        onValueChange = {
            content.value.value = it
        },
        visualTransformation = {
            val t = codeString(content.value.value)
            TransformedText(t, OffsetMapping.Identity)
        },
        singleLine = true,
        modifier = modifier
    )

private fun codeString(str: String) = buildAnnotatedString {
    val theme = EditorTheme.get()
    withStyle(theme.code.simple) {
        val strFormatted = str.replace("\t", "    ")
        append(strFormatted)
        addStyle(theme.code.punctuation, strFormatted, ":")
        addStyle(theme.code.punctuation, strFormatted, "=")
        addStyle(theme.code.punctuation, strFormatted, "\"")
        addStyle(theme.code.punctuation, strFormatted, "[")
        addStyle(theme.code.punctuation, strFormatted, "]")
        addStyle(theme.code.punctuation, strFormatted, "{")
        addStyle(theme.code.punctuation, strFormatted, "}")
        addStyle(theme.code.punctuation, strFormatted, "(")
        addStyle(theme.code.punctuation, strFormatted, ")")
        addStyle(theme.code.punctuation, strFormatted, ",")
        addStyle(theme.code.keyword, strFormatted, "fun ")
        addStyle(theme.code.keyword, strFormatted, "---")
        addStyle(theme.code.keyword, strFormatted, "val ")
        addStyle(theme.code.keyword, strFormatted, "var ")
        addStyle(theme.code.keyword, strFormatted, "private ")
        addStyle(theme.code.keyword, strFormatted, "internal ")
        addStyle(theme.code.keyword, strFormatted, "for ")
        addStyle(theme.code.keyword, strFormatted, "expect ")
        addStyle(theme.code.keyword, strFormatted, "actual ")
        addStyle(theme.code.keyword, strFormatted, "import ")
        addStyle(theme.code.keyword, strFormatted, "package ")
        addStyle(theme.code.value, strFormatted, "true")
        addStyle(theme.code.value, strFormatted, "false")
        addStyle(theme.code.value, str, Regex("[0-9]*"))
        addStyle(theme.code.header, str, Regex("^#.*"))
        addStyle(theme.code.table, str, Regex("^\\|.*"))
        addStyle(theme.code.quote, str, Regex("^>.*"))
        addStyle(theme.code.quote, str, Regex("^-.*"))
        addStyle(theme.code.value, strFormatted, Regex("[0-9]*"))
        addStyle(theme.code.annotation, strFormatted, Regex("^@[a-zA-Z_]*"))
        addStyle(theme.code.comment, strFormatted, Regex("^\\s*//.*"))
    }
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: String) {
    addStyle(style, text, fromLiteral(regexp))
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: Regex) {
    for (result in regexp.findAll(text)) {
        addStyle(style, result.range.first, result.range.last + 1)
    }
}

private val loadingKey = Any()

@Composable
fun <T : Any> loadableScoped(load: CoroutineScope.() -> T): MutableState<T?> {
    val state: MutableState<T?> = remember { mutableStateOf(null) }
    LaunchedEffect(loadingKey) {
        try {
            state.value = load()
        } catch (e: CancellationException) {
            // ignore
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return state
}