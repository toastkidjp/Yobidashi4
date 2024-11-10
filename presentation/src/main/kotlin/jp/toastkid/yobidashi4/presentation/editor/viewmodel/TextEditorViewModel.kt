package jp.toastkid.yobidashi4.presentation.editor.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.text.TextFieldScrollState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.finder.FindOrderReceiver
import jp.toastkid.yobidashi4.presentation.editor.keyboard.KeyEventConsumer
import jp.toastkid.yobidashi4.presentation.editor.keyboard.PreviewKeyEventConsumer
import jp.toastkid.yobidashi4.presentation.editor.transformation.TextEditorVisualTransformation
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class)
class TextEditorViewModel : KoinComponent {

    private var tab: EditorTab = EditorTab(Path.of(""))

    private val mainViewModel: MainViewModel by inject()

    private val content = mutableStateOf(TextFieldValue())

    private var lastParagraph: MultiParagraph? = null

    private val altPressed = AtomicBoolean(false)

    private val lineCount = mutableStateOf(0)

    private val keyEventConsumer = KeyEventConsumer()

    private val previewKeyEventConsumer = PreviewKeyEventConsumer()

    private val verticalScrollState = TextFieldScrollState(Orientation.Vertical, 0)

    private val lineNumberScrollState = ScrollState(0)

    private val focusRequester = FocusRequester()

    private val findOrderReceiver = FindOrderReceiver()

    private val setting: Setting by inject()

    private val conversionLimit = setting.editorConversionLimit()

    fun content() = content.value

    fun onValueChange(it: TextFieldValue) {
        if (altPressed.get()) {
            return
        }

        applyStyle(it)
    }

    private fun applyStyle(it: TextFieldValue) {
        val newContent = if (tab.editable()) it else it.copy(text = content.value.text)
        if (content.value.text != newContent.text) {
            mainViewModel.updateEditorContent(
                tab.path,
                newContent.text,
                -1,
                resetEditing = false
            )
        }

        content.value = newContent
    }

    private val lineHeights = mutableMapOf<Int, TextUnit>()

    fun setMultiParagraph(multiParagraph: MultiParagraph) {
        lastParagraph = multiParagraph
        if (lineCount.value != multiParagraph.lineCount) {
            lineCount.value = multiParagraph.lineCount
        }

        val lastLineHeights = (0 until lineCount.value).associateWith { multiParagraph.getLineHeight(it) }
        val distinct = lastLineHeights.values.distinct()
        val max = distinct.max()
        lineHeights.clear()
        lastLineHeights.forEach { lineHeights.put(it.key, (1.55f * it.value / max).em) }
    }

    fun getLineHeight(lineNumber: Int): TextUnit {
        return lineHeights.getOrElse(lineNumber, { 1.55.em })
    }

    fun verticalScrollState() = verticalScrollState

    fun lineNumberScrollState() = lineNumberScrollState

    fun onClickLineNumber(it: Int) {
        val multiParagraph = lastParagraph ?: return

        content.value = content.value.copy(
            selection = TextRange(multiParagraph.getLineStart(it), multiParagraph.getLineEnd(it))
        )
    }

    fun focusRequester() = focusRequester

    fun onKeyEvent(it: KeyEvent): Boolean {
        altPressed.set(it.isAltPressed)

        return keyEventConsumer(
            it,
            content.value,
            lastParagraph,
            ::applyStyle
        )
    }

    fun onPreviewKeyEvent(it: KeyEvent, coroutineScope: CoroutineScope): Boolean {
        return previewKeyEventConsumer.invoke(
            it,
            content.value,
            lastParagraph,
            ::applyStyle
        ) {
            coroutineScope.launch {
                verticalScrollState.scrollBy(it)
            }
        }
    }

    suspend fun adjustLineNumberState() {
        lineNumberScrollState.scrollTo(verticalScrollState.offset.toInt())
    }

    fun initialScroll(coroutineScope: CoroutineScope, ms: Long = 150) {
        if (tab.scroll() <= 0.0) {
            focusRequester().requestFocus()
            return
        }

        coroutineScope.launch {
            focusRequester().requestFocus()
            delay(ms)
            verticalScrollState.offset = tab.scroll().toFloat()
        }
    }

    fun lineNumbers(): List<Pair<Int, String>> {
        val max = lineCount.value
        val length = max.toString().length
        return (1 .. max).map {
            val lineNumberCount = it
            val fillCount = length - lineNumberCount.toString().length
            return@map (it - 1 to with(StringBuilder()) {
                repeat(fillCount) {
                    append(" ")
                }
                append(lineNumberCount)
            }.toString())
        }
    }

    fun launchTab(tab: EditorTab, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        this.tab = tab

        val newContent = TextFieldValue(tab.getContent().toString(), TextRange(tab.caretPosition()))
        applyStyle(newContent)

        CoroutineScope(dispatcher).launch {
            mainViewModel.finderFlow().collect {
                findOrderReceiver(it, content.value, ::applyStyle)
            }
        }
    }

    fun currentLineOffset(): Offset {
        val paragraph = lastParagraph ?: return Offset.Zero
        val currentLine = paragraph.getLineForOffset(content.value.selection.start)
        return Offset(paragraph.getLineLeft(currentLine), paragraph.getLineTop(currentLine) - verticalScrollState.offset)
    }

    fun currentLineHighlightColor(): Color {
        return Color(
            if (mainViewModel.darkMode()) 0xCC666239
            else 0xCCFFF9AF
        )
    }

    private val visualTransformation = TextEditorVisualTransformation(content, mainViewModel.darkMode())

    fun visualTransformation(): VisualTransformation {
        if (content.value.text.length > conversionLimit) {
            return VisualTransformation.None
        }

        return visualTransformation
    }

    fun makeCharacterCountMessage(count: Int): String {
        return "Character: $count"
    }

    fun dispose() {
        val currentText = content.value.text
        if (currentText.isNotEmpty()) {
            mainViewModel.updateEditorContent(
                tab.path,
                currentText,
                content.value.selection.start,
                verticalScrollState.offset.toDouble(),
                resetEditing = false
            )
        }

        lastParagraph = null
        content.value = TextFieldValue()
    }

}

private const val CONVERSION_LIMIT_LENGTH = 4500
