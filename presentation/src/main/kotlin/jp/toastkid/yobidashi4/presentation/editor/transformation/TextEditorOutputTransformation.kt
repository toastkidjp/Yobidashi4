/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.transformation

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import java.util.concurrent.atomic.AtomicReference
import java.util.regex.Pattern

@Immutable
private data class EditorStyle2(
    val regex: Pattern,
    val lightStyle: SpanStyle,
    val darkStyle: SpanStyle
)

class TextEditorOutputTransformation(
    private val content: TextFieldState,
    private val darkMode: Boolean
) : OutputTransformation {

    private val patterns = listOf(
        EditorStyle2(
            Pattern.compile("[0-9]*", Pattern.MULTILINE),
            SpanStyle(Color(0xFF6897BB)),
            SpanStyle(Color(0xFFA8B7EE))
        ),
        EditorStyle2(
            Pattern.compile("^#.*?$", Pattern.MULTILINE),
            SpanStyle(Color(0xFF008800), fontWeight = FontWeight.Bold),
            SpanStyle(Color(0xFF00DD00), fontWeight = FontWeight.Bold)
        ),
        EditorStyle2(
            Pattern.compile("^\\|.*?$", Pattern.MULTILINE),
            SpanStyle(Color(0xFF8800CC)),
            SpanStyle(Color(0xFF86EEC7))
        ),
        EditorStyle2(
            Pattern.compile("^>.*?$", Pattern.MULTILINE),
            SpanStyle(Color(0xFF7744AA)),
            SpanStyle(Color(0xFFCCAAFF))
        ),
        EditorStyle2(
            Pattern.compile("^-.*?$", Pattern.MULTILINE),
            SpanStyle(Color(0xFF666239)),
            SpanStyle(Color(0xFFFFD54F))
        ),
        EditorStyle2(
            Pattern.compile("^\\*.*?$", Pattern.MULTILINE),
            SpanStyle(Color(0xFF666239)),
            SpanStyle(Color(0xFFFFD54F))
        )
    )

    private val styleCache = mutableListOf<Triple<Int, Int, SpanStyle>>()

    private val transformedText = AtomicReference<CharSequence?>(null)

    override fun TextFieldBuffer.transformOutput() {
        val last = transformedText.get()
        if (last != null && content.composition == null && last == content.text) {
            applyStyles(this)
            return
        }

        transformedText.set(content.text)
        calculateStyle(darkMode, content.text.toString())
        applyStyles(this)
    }

    private fun applyStyles(buffer: TextFieldBuffer) {
        styleCache.forEach { triple ->
            buffer.addStyle(triple.third, triple.first, triple.second)
        }
    }

    private fun calculateStyle(darkTheme: Boolean, str: String) {
        styleCache.clear()
        patterns.forEach { pattern ->
            val find = pattern.regex.matcher(str)
            while (find.find()) {
                val spanStyle = if (darkTheme) pattern.darkStyle else pattern.lightStyle
                styleCache.add(Triple(find.start(), find.end(), spanStyle))
            }
        }
    }

}