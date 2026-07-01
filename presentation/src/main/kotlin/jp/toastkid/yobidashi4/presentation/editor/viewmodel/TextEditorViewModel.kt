/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import jp.toastkid.yobidashi4.domain.model.find.FindOrder
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.data.ConversionTrigger
import jp.toastkid.yobidashi4.presentation.editor.data.LineNumber
import jp.toastkid.yobidashi4.presentation.editor.finder.FindOrderReceiver
import jp.toastkid.yobidashi4.presentation.editor.keyboard.KeyEventConsumer
import jp.toastkid.yobidashi4.presentation.editor.keyboard.PreviewKeyEventHandler
import jp.toastkid.yobidashi4.presentation.editor.transformation.ParseResult
import jp.toastkid.yobidashi4.presentation.editor.transformation.TextEditorOutputTransformation
import jp.toastkid.yobidashi4.presentation.editor.usecase.TextEditorOperationUseCase
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path
import java.text.DecimalFormat
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
class TextEditorViewModel : KoinComponent {

    private val tab = AtomicReference(EditorTab(Path.of("")))

    private val mainViewModel: MainViewModel by inject()

    private val content = TextFieldState()

    private val lastParagraph = AtomicReference<MultiParagraph?>(null)

    private val altPressed = AtomicBoolean(false)

    private val lineCount = mutableStateOf(0)

    private val keyEventConsumer = KeyEventConsumer()

    private val scrollEventFlow = MutableSharedFlow<Float>(extraBufferCapacity = 1)

    fun scrollEventFlow(): SharedFlow<Float> = scrollEventFlow

    fun emitScrollEvent(value: Float) {
        scrollEventFlow.tryEmit(value)
    }

    private val textEditorOperationUseCase = TextEditorOperationUseCase(
        mainViewModel,
        content,
        {
            lastParagraph.get() },
        { emitScrollEvent(it) },
        { switchShowLineNumber() }
    )

    private val previewKeyEventHandler = PreviewKeyEventHandler(textEditorOperationUseCase)

    private val verticalScrollState = ScrollState(0)

    private val lineNumberScrollState = ScrollState(0)

    private val focusRequester = FocusRequester()

    private val findOrderReceiver = FindOrderReceiver()

    private val setting: Setting by inject()

    private val conversionLimit = setting.editorConversionLimit()

    private val formatter = DecimalFormat("#,###.##")

    private val parseResult = mutableStateOf(ParseResult("", emptyList()))

    private val transformation =
        TextEditorOutputTransformation(content, mainViewModel.darkMode(), { parseResult.value })

    fun content() = content

    fun update() {
        val tab = this.tab.get()
        mainViewModel.updateEditorContent(
            tab.path,
            content.text,
            -1,
            resetEditing = false
        )
    }

    private fun applyStyle(it: TextFieldState) {
        val tab = this.tab.get()
        val newContent = if (tab.editable()) it else it//TODO .copy(text = content.text)

        content.setTextAndPlaceCursorAtEnd(newContent.text.toString())
        content.edit {
            selection = it.selection
        }
    }

    private val lineHeights = mutableMapOf<Int, TextUnit>()

    fun setMultiParagraph(multiParagraph: MultiParagraph) {
        lastParagraph.set(multiParagraph)
        if (lineCount.value != multiParagraph.lineCount) {
            lineCount.value = multiParagraph.lineCount
        }

        setNewCurrentLineHighlight(multiParagraph)

        val lastLineHeights = (0 until lineCount.value).associateWith(multiParagraph::getLineHeight)
        val distinct = lastLineHeights.values.distinct()
        val max = distinct.max()
        lineHeights.clear()
        lastLineHeights.forEach {
            val baseline = it.value
            lineHeights.put(it.key, ((baseline / max) * 1.5f).em)
        }
    }

    private fun setNewCurrentLineHighlight(multiParagraph: MultiParagraph) {
        val cursorOffset = min(
            multiParagraph.intrinsics.annotatedString.text.length,
            content.selection.start
        )
        val cursorRect = multiParagraph.getCursorRect(cursorOffset)
        val cursorSize = (cursorRect.bottom - cursorRect.top)
        highlightSize.set(Size(Float.MAX_VALUE, cursorSize.em.value))
    }

    private val highlightSize = AtomicReference(Size(Float.MAX_VALUE, 37.em.value))

    fun getHighlightSize(): Size = highlightSize.get()

    fun verticalScrollState() = verticalScrollState

    fun lineNumberScrollState() = lineNumberScrollState

    fun onClickLineNumber(it: Int) {
        val multiParagraph = lastParagraph.get() ?: return

        content.edit {
            selection = TextRange(multiParagraph.getLineStart(it), multiParagraph.getLineEnd(it))
        }
    }

    fun focusRequester() = focusRequester

    fun onKeyEvent(it: KeyEvent): Boolean {
        return keyEventConsumer(
            it,
            content,
            lastParagraph.get(),
        )
    }

    fun onPreviewKeyEvent(keyEvent: KeyEvent): Boolean {
        altPressed.set(keyEvent.isAltPressed)

        return previewKeyEventHandler.invoke(keyEvent)
    }

    suspend fun adjustLineNumberState() {
        lineNumberScrollState.scrollTo(verticalScrollState.value)
    }

    fun initialScroll(coroutineScope: CoroutineScope, ms: Long = 150) {
        val tab = this.tab.get()
        if (tab.scroll() <= 0.0) {
            focusRequester().requestFocus()
            return
        }

        coroutineScope.launch {
            focusRequester().requestFocus()
            delay(ms)
            verticalScrollState.scrollTo(tab.scroll().toInt())
        }
    }

    fun lineNumbers(): List<LineNumber> {
        val max = lineCount.value
        val length = max.toString().length
        return (1 .. max).map {
            val fillCount = length - it.toString().length
            return@map LineNumber(
                it - 1,
                with(StringBuilder()) {
                    repeat(fillCount) {
                        append(" ")
                    }
                    append(it)
                }.toString()
            )
        }
    }

    fun launchTab(tab: EditorTab, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        this.tab.set(tab)

        val newContent = TextFieldState(tab.getContent().toString(), TextRange(tab.caretPosition()))
        applyStyle(newContent)
        update()
        CoroutineScope(dispatcher).launch {
            mainViewModel.finderFlow().collect(::invokeFindAction)
        }
    }

    private fun invokeFindAction(order: FindOrder) {
        findOrderReceiver(order, content)
    }

    fun currentLineOffset(): Offset {
        val paragraph = lastParagraph.get() ?: return Offset.Zero
        val currentLine = paragraph.getLineForOffset(content.selection.start)
        return Offset(
            paragraph.getLineLeft(currentLine),
            paragraph.getLineTop(currentLine) - verticalScrollState.value
        )
    }

    fun currentLineHighlightColor(): Color {
        return Color(
            if (mainViewModel.darkMode()) 0xCC666239
            else 0xCCFFF9AF
        )
    }

    fun inputTransformation(): InputTransformation {
        return InputTransformation {
            if (altPressed.get()) {
                revertAllChanges()
                return@InputTransformation
            }
        }
    }

    private val none = OutputTransformation {
        append("[EOF]")
    }

    fun visualTransformation(): OutputTransformation {
        if (content.text.length > conversionLimit) {
            return none
        }

        return transformation
    }

    fun makeCharacterCountMessage(count: Int): String {
        return "Character: ${formatter.format(count)}"
    }

    fun fontSize() = setting.editorFontSize()

    fun lineHeight() = setting.editorLineHeight()

    fun dispose() {
        val currentText = content.text
        if (currentText.isNotEmpty()) {
            mainViewModel.updateEditorContent(
                tab.get().path,
                currentText,
                content.selection.start,
                verticalScrollState.value.toDouble(),
                resetEditing = false
            )
        }

        lastParagraph.set(null)
        content.edit {
            delete(0, length)
        }
    }

    fun calculateConversionTrigger(): ConversionTrigger {
        val text = content.text

        val lineCount = lineNumbers().size

        val lineStarts = run {
            if (text.isEmpty()) return@run ""

            val sb = StringBuilder(lineCount)

            sb.append(text[0])

            var index = text.indexOf('\n')
            while (index != -1 && index < text.lastIndex) {
                val nextChar = text[index + 1]
                sb.append(nextChar)

                index = text.indexOf('\n', index + 1)
            }

            sb.toString()
        }

        return ConversionTrigger(lineCount, lineStarts, content.composition == null)
    }

    fun parseContent() {
        val currentText = content.text.toString()
        val styles = calculateStyleAsync(mainViewModel.darkMode(), currentText)

        parseResult.value = ParseResult(currentText, styles)
    }

    private fun calculateStyleAsync(darkTheme: Boolean, str: String): List<Triple<Int, Int, SpanStyle>> {
        val list = mutableListOf<Triple<Int, Int, SpanStyle>>()

        transformation.getPatterns().forEach { pattern ->
            val find = pattern.regex.matcher(str)
            while (find.find()) {
                val spanStyle = if (darkTheme) pattern.darkStyle else pattern.lightStyle
                list.add(Triple(find.start(), find.end(), spanStyle))
            }
        }
        return list
    }

    private val showLineNumber = mutableStateOf(true)

    fun showLineNumber(): Boolean = showLineNumber.value

    fun switchShowLineNumber() {
        showLineNumber.value = showLineNumber.value.not()
    }

}
