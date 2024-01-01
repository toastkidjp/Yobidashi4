package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import java.util.regex.Pattern
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MarkdownPreviewViewModel(scrollState: ScrollableState) : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val scrollAction = KeyboardScrollAction(scrollState)

    fun onKeyEvent(coroutineScope: CoroutineScope, it: KeyEvent): Boolean {
        val scrollActionConsumed = scrollAction(coroutineScope, it.key, it.isCtrlPressed)
        if (scrollActionConsumed) {
            return true
        }

        if (it.type != KeyEventType.KeyUp) {
            return false
        }

        if (it.isCtrlPressed && it.isShiftPressed && it.key == Key.O) {
            mainViewModel.webSearch(mainViewModel.selectedText())
            return true
        }

        return false
    }

    fun annotate(text: String, finderTarget: String?) = buildAnnotatedString {
        var lastIndex = 0
        val matcher = internalLinkPattern.matcher(text)
        while (matcher.find()) {
            val title = matcher.group(1)
            val url = matcher.group(2)
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            val extracted = text.substring(lastIndex, startIndex)
            if (extracted.isNotEmpty()) {
                append(extracted)
            }

            val annotateStart = length
            append(title)
            addStyle(
                style = SpanStyle(
                    color = Color(0xff64B5F6),
                    textDecoration = TextDecoration.Underline
                ), start = annotateStart, end = annotateStart + title.length
            )

            // attach a string annotation that stores a URL to the text "link"
            addStringAnnotation(
                tag = "URL",
                annotation = url,
                start = annotateStart,
                end = annotateStart + title.length
            )
            lastIndex = endIndex
        }

        if (lastIndex >= text.length) {
            return@buildAnnotatedString
        }

        appendStyleIfNeed(text, lastIndex)

        if (!finderTarget.isNullOrBlank()) {
            val finderMatcher = Pattern.compile(finderTarget).matcher(text)
            while (finderMatcher.find()) {
                addStyle(
                    style = SpanStyle(
                        color = Color(0xFF00AAFF),
                        background = Color(0xFFFFFFFF)
                    ), start = finderMatcher.start(), end = finderMatcher.end()
                )
            }
        }
    }

    private fun AnnotatedString.Builder.appendStyleIfNeed(text: String, lastIndex: Int) {
        if (text.contains("~~")) {
            applyStylePattern(
                text,
                lastIndex,
                lineThroughPattern,
                "~~",
                SpanStyle(textDecoration = TextDecoration.LineThrough)
            )
            return
        }

        if (text.contains("***")) {
            applyStylePattern(text, lastIndex, italicPattern, "***", SpanStyle(fontStyle = FontStyle.Italic))
            return
        }

        if (text.contains("**")) {
            applyStylePattern(text, lastIndex, boldingPattern, "**", SpanStyle(fontWeight = FontWeight.Bold))
            return
        }

        append(text.substring(lastIndex, text.length))
    }

    private fun AnnotatedString.Builder.applyStylePattern(
        text: String,
        lastIndex: Int,
        pattern: Pattern,
        replacementTarget: String,
        spanStyle: SpanStyle
    ) {
        val m = pattern.matcher(text)
        append(text.substring(lastIndex, text.length).replace(replacementTarget, ""))
        val offset = replacementTarget.length * 2
        m.results().toList().forEachIndexed { index, matchResult ->
            addStyle(
                spanStyle,
                matchResult.start() - (offset * index),
                matchResult.end() - (offset * (index + 1))
            )
        }
    }

}

private val internalLinkPattern =
    Pattern.compile("\\[(.+?)\\]\\((.+?)\\)", Pattern.DOTALL)

private val lineThroughPattern =
    Pattern.compile("~~(.+?)~~", Pattern.DOTALL)

private val boldingPattern =
    Pattern.compile("\\*\\*(.+?)\\*\\*", Pattern.DOTALL)

private val italicPattern =
    Pattern.compile("\\*\\*\\*(.+?)\\*\\*\\*", Pattern.DOTALL)