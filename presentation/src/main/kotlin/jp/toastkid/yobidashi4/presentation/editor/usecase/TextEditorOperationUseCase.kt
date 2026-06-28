/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.usecase

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.math.min

class TextEditorOperationUseCase(
    private val mainViewModel: MainViewModel,
    private val content: TextFieldState,
    private val lastParagraph: () -> MultiParagraph?,
    private val scrollBy: (Float) -> Unit,
    private val switchLineNumber: () -> Unit
) {

    fun moveToTop() {
        content.edit {
            selection = TextRange.Zero
        }
    }

    fun moveToBottom() {
        content.edit {
            selection = TextRange(length)
        }
    }

    fun scrollBy(spValue: Float) {
        scrollBy.invoke(spValue)
    }

    fun cutLine(): Boolean {
        if (content.selection.start != content.selection.end) {
            return false
        }

        val textLayoutResult = lastParagraph() ?: return false
        val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
        val lineStart = textLayoutResult.getLineStart(currentLine)
        val lineEnd = textLayoutResult.getLineEnd(currentLine)
        val targetEnd = min(content.text.length, lineEnd + 1)
        val currentLineText = content.text.substring(lineStart, targetEnd)
        ClipboardPutterService().invoke(currentLineText)
        content.edit { delete(lineStart, targetEnd) }
        return true
    }

    fun deleteLine() {
        if (content.selection.start != content.selection.end) {
            return
        }

        val textLayoutResult = lastParagraph() ?: return
        val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
        val lineStart = textLayoutResult.getLineStart(currentLine)
        val lineEnd = textLayoutResult.getLineEnd(currentLine)
        content.edit {
            delete(lineStart, min(length, lineEnd + 1))
        }
    }

    fun switchArticleList() {
        if (mainViewModel.openArticleList().not()) {
            mainViewModel.switchArticleList()
        }
    }

    fun hideArticleList() {
        mainViewModel.hideArticleList()
    }

    fun switchLineNumber() {
        switchLineNumber.invoke()
    }

}