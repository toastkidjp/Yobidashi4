package jp.toastkid.yobidashi4.presentation.editor.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.text.TextFieldScrollState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.EditorTheme
import jp.toastkid.yobidashi4.presentation.editor.keyboard.KeyEventConsumer
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    private val job = Job()

    private val keyEventConsumer = KeyEventConsumer()

    private val theme = EditorTheme()

    private val verticalScrollState = TextFieldScrollState(Orientation.Vertical, 0)

    private val adapter = ScrollbarAdapter(verticalScrollState)

    private val lineNumberScrollState = ScrollState(0)

    private val focusRequester = FocusRequester()

    private var lastConversionJob: Job? = null

    fun content() = content.value

    fun onValueChange(it: TextFieldValue) {
        lastConversionJob?.cancel()
        val forceEfficientMode = content.value.text.length > 6000
        val notInComposition = it.composition == null
        if (notInComposition && content.value.text.length != it.text.length) {
            mainViewModel.updateEditorContent(
                tab.path,
                content.value.text,
                -1,
                resetEditing = false
            )
        }

        content.value = it

        if (forceEfficientMode || !notInComposition) {
            return
        }

        lastConversionJob = CoroutineScope(Dispatchers.IO).launch {
            delay(1500)
            content.value =  it.copy(theme.codeString(it.text, mainViewModel.darkMode()))
        }
    }

    fun setMultiParagraph(multiParagraph: MultiParagraph) {
        lastParagraph = multiParagraph
    }

    fun verticalScrollState() = verticalScrollState

    fun scrollbarAdapter() = adapter

    fun lineNumberScrollState() = lineNumberScrollState

    fun maxLineCount(): Int {
        return lastParagraph?.lineCount ?: content.value.text.split("\n").size
    }

    fun onClickLineNumber(it: Int) {
        val multiParagraph = lastParagraph ?: return

        content.value = content.value.copy(
            selection = TextRange(multiParagraph.getLineStart(it), multiParagraph.getLineEnd(it))
        )
    }

    fun focusRequester() = focusRequester

    fun onKeyEvent(it: KeyEvent, coroutineScope: CoroutineScope): Boolean {
        return keyEventConsumer(
            it,
            tab.path,
            content.value,
            lastParagraph,
            {
                coroutineScope.launch {
                    verticalScrollState.scrollBy(it.sp.value)
                }
            },
            { content.value = it }
        )
    }

    suspend fun adjustLineNumberState() {
        lineNumberScrollState.scrollTo(verticalScrollState.offset.toInt())
    }

    fun initialScroll(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            delay(500)
            adapter.scrollTo(tab.scroll())
        }
    }

    fun launchTab(tab: EditorTab, coroutineScope: CoroutineScope) {
        this.tab = tab
        focusRequester.requestFocus()

        content.value = TextFieldValue(theme.codeString(tab.getContent(), mainViewModel.darkMode()), TextRange(tab.caretPosition()))

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
                if (selected == -1) {
                    return@collect
                }

                content.value = content.value.copy(selection = TextRange(selected, selected + it.target.length))
            }
        }
    }

    fun dispose() {
        val currentText = content.value.text
        if (currentText.isNotEmpty()) {
            mainViewModel.updateEditorContent(tab.path, currentText, content.value.selection.start, verticalScrollState.offset.toDouble(), resetEditing = false)
        }

        lastParagraph = null
        content.value = TextFieldValue()
        job.cancel()
    }

}