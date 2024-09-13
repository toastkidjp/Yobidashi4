package jp.toastkid.yobidashi4.presentation.lib.text

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import kotlin.math.min

class KeywordHighlighter {

    operator fun invoke(text: String, finderTarget: String? = null) = buildAnnotatedString {
        var lastIndex = 0
        val matcher = internalLinkPattern.matcher(text)
        while (matcher.find()) {
            val title = matcher.group(1)
            val url = matcher.group(2)
            val startIndex = matcher.start()

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
            lastIndex = matcher.end()
        }

        if (lastIndex >= text.length) {
            return@buildAnnotatedString
        }

        appendStyleIfNeed(text, lastIndex)

        if (!finderTarget.isNullOrBlank()) {
            val pattern = try {
                Pattern.compile(finderTarget)
            } catch (e: PatternSyntaxException) {
                return@buildAnnotatedString
            }

            val buildString = toAnnotatedString().text

            val finderMatcher = pattern.matcher(buildString)
            while (finderMatcher.find()) {
                addStyle(
                    style = SpanStyle(
                        color = Color(0xFFFFFFFF),
                        background = Color(0xFF444499)
                    ), start = finderMatcher.start(), end = finderMatcher.end()
                )
            }
        }
    }

    private fun AnnotatedString.Builder.appendStyleIfNeed(text: String, lastIndex: Int) {
        val end = min(lastIndex, text.length)
        if (text.contains("~~")) {
            applyStylePattern(
                text,
                end,
                lineThroughPattern,
                "~~",
                SpanStyle(textDecoration = TextDecoration.LineThrough)
            )
            return
        }

        if (text.contains("***")) {
            applyStylePattern(text, end, italicPattern, "***", SpanStyle(fontStyle = FontStyle.Italic))
            return
        }

        if (text.contains("**")) {
            applyStylePattern(text, end, boldingPattern, "**", SpanStyle(fontWeight = FontWeight.Bold))
            return
        }

        append(text.substring(end))
    }

    private fun AnnotatedString.Builder.applyStylePattern(
        text: String,
        lastIndex: Int,
        pattern: Pattern,
        replacementTarget: String,
        spanStyle: SpanStyle
    ) {
        val matcher = pattern.matcher(text)
        append(text.substring(lastIndex).replace(replacementTarget, ""))
        val offset = replacementTarget.length * 2
        matcher.results().toList().forEachIndexed { index, matchResult ->
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