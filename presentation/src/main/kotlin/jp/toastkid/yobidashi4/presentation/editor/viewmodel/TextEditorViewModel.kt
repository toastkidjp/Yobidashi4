package jp.toastkid.yobidashi4.presentation.editor.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.text.TextFieldScrollState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.finder.FinderMessageFactory
import jp.toastkid.yobidashi4.presentation.editor.keyboard.KeyEventConsumer
import jp.toastkid.yobidashi4.presentation.editor.style.EditorTheme
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class)
class TextEditorViewModel {

    private var tab: EditorTab = EditorTab(Path.of(""))

    private val mainViewModel = object : KoinComponent { val vm: MainViewModel by inject() }.vm

    private val content = mutableStateOf(TextFieldValue())

    private var lastParagraph: MultiParagraph? = null

    private var altPressed = false

    private val lineCount = mutableStateOf(0)

    private val keyEventConsumer = KeyEventConsumer()

    private val theme = EditorTheme()

    private val verticalScrollState = TextFieldScrollState(Orientation.Vertical, 0)

    private val adapter = ScrollbarAdapter(verticalScrollState)

    private val lineNumberScrollState = ScrollState(0)

    private val focusRequester = FocusRequester()

    private val finderMessageFactory = FinderMessageFactory()

    fun content() = content.value

    fun onValueChange(it: TextFieldValue) {
        if (altPressed) {
            return
        }

        if (content.value.text != it.text) {
            mainViewModel.updateEditorContent(
                tab.path,
                it.text,
                -1,
                resetEditing = false
            )
        }

        applyStyle(it)
    }

    private fun applyStyle(it: TextFieldValue) {
        content.value = it
    }

    fun setMultiParagraph(multiParagraph: MultiParagraph) {
        lastParagraph = multiParagraph
        if (lineCount.value != multiParagraph.lineCount) {
            lineCount.value = multiParagraph.lineCount
        }
    }

    fun verticalScrollState() = verticalScrollState

    fun scrollbarAdapter() = adapter

    fun lineNumberScrollState() = lineNumberScrollState

    fun onClickLineNumber(it: Int) {
        val multiParagraph = lastParagraph ?: return

        content.value = content.value.copy(
            selection = TextRange(multiParagraph.getLineStart(it), multiParagraph.getLineEnd(it))
        )
    }

    fun focusRequester() = focusRequester

    fun onKeyEvent(it: KeyEvent, coroutineScope: CoroutineScope): Boolean {
        altPressed = it.isAltPressed

        if (it.type != KeyEventType.KeyUp) {
            return false
        }

        return keyEventConsumer(
            it,
            content.value,
            lastParagraph,
            {
                coroutineScope.launch {
                    verticalScrollState.scrollBy(it.sp.value)
                }
            },
            {
                applyStyle(it)
            }
        )
    }

    suspend fun adjustLineNumberState() {
        lineNumberScrollState.scrollTo(verticalScrollState.offset.toInt())
    }

    fun initialScroll(coroutineScope: CoroutineScope, ms: Long = 500) {
        coroutineScope.launch {
            delay(ms)
            adapter.scrollTo(tab.scroll())
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

    fun launchTab(tab: EditorTab) {
        this.tab = tab
        focusRequester.requestFocus()

        val newContent = TextFieldValue(tab.getContent(), TextRange(tab.caretPosition()))
        applyStyle(newContent)

        var selected = -1
        CoroutineScope(Dispatchers.IO).launch {
            mainViewModel.finderFlow().collect {
                if (it.invokeReplace) {
                    content.value = TextFieldValue(content.value.text.replace(it.target, it.replace, it.caseSensitive.not()))
                    return@collect
                }
                selected =
                    if (it.upper) content.value.text.lastIndexOf(it.target, selected - 1, it.caseSensitive.not())
                    else content.value.text.indexOf(it.target, selected + 1, it.caseSensitive.not())

                val foundCount = if (it.target.isBlank()) 0 else content.value.text.split(it.target).size - 1
                mainViewModel.setFindStatus(finderMessageFactory(it.target, foundCount))

                if (selected == -1) {
                    return@collect
                }

                content.value = content.value.copy(selection = TextRange(selected, selected + it.target.length))
            }
        }
    }

    fun currentLineOffset(): Offset {
        val paragraph = lastParagraph ?: return Offset.Unspecified
        val currentLine = paragraph.getLineForOffset(content.value.selection.start)
        return Offset(paragraph.getLineLeft(currentLine), paragraph.getLineTop(currentLine) - verticalScrollState.offset)
    }

    fun currentLineHighlightColor(): Color {
        return Color(
            if (mainViewModel.darkMode()) 0xCC666239
            else 0xCCFFF9AF
        )
    }

    private var transformedText: TransformedText? = null

    private val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int =
            if (offset >= content.value.text.length) content.value.text.length else offset

        override fun transformedToOriginal(offset: Int): Int =
            if (offset >= content.value.text.length) content.value.text.length else offset
    }

    fun visualTransformation(): VisualTransformation {
        if (content.value.text.length > CONVERSION_LIMIT_LENGTH) {
            return VisualTransformation.None
        }

        return VisualTransformation { text ->
            val last = transformedText
            if (last != null && content.value.composition == null && last.text.text == text.text) {
                return@VisualTransformation last
            }

            val new = TransformedText(theme.codeString(text.text, mainViewModel.darkMode()), offsetMapping)
            transformedText = new
            return@VisualTransformation new
        }
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
        transformedText = null
        content.value = TextFieldValue()
    }

}

private const val CONVERSION_LIMIT_LENGTH = 4500
