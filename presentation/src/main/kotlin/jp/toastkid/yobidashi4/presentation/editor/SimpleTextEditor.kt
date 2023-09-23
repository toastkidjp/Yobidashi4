package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.rememberTextFieldVerticalScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.keyboard.KeyEventConsumer
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SimpleTextEditor(
    tab: EditorTab,
    setStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val content = remember { mutableStateOf(TextFieldValue()) }
    val verticalScrollState = rememberTextFieldVerticalScrollState()
    val lineNumberScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val lastParagraph = remember { mutableStateOf<MultiParagraph?>(null) }
    val job = remember { Job() }
    val coroutineScope = rememberCoroutineScope()

    val mainViewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }

    val keyEventConsumer = remember { KeyEventConsumer() }
    val theme = remember { EditorTheme() }

    Box {
        var altPressed = false

        BasicTextField(
            value = content.value,
            onValueChange = {
                if (altPressed) {
                    return@BasicTextField
                }
                val inComposition = it.composition == null
                if (inComposition && content.value.text.length != it.text.length) {
                    setStatus("Character: ${it.text.length}")
                    mainViewModel.updateEditorContent(
                        tab.path,
                        content.value.text,
                        -1,
                        resetEditing = false
                    )
                }
                content.value = if (inComposition) it.copy(theme.codeString(it.text, mainViewModel.darkMode())) else it
            },
            onTextLayout = {
                lastParagraph.value = it.multiParagraph
            },
            decorationBox = {
                Row {
                    Column(
                        modifier = Modifier
                            .verticalScroll(lineNumberScrollState)
                            .padding(horizontal = 8.dp)
                            .wrapContentSize(unbounded = true)
                    ) {
                        val max = lastParagraph.value?.lineCount ?: content.value.text.split("\n").size
                        val length = max.toString().length
                        repeat(max) {
                            val lineNumberCount = it + 1
                            val fillCount = length - lineNumberCount.toString().length
                            val lineNumberText = with(StringBuilder()) {
                                repeat(fillCount) {
                                    append(" ")
                                }
                                append(lineNumberCount)
                            }.toString()
                            Box(
                                contentAlignment = Alignment.CenterEnd,
                                modifier = Modifier.clickable {
                                    val multiParagraph = lastParagraph.value ?: return@clickable

                                    content.value = content.value.copy(
                                        selection = TextRange(multiParagraph.getLineStart(it), multiParagraph.getLineEnd(it))
                                    )
                                }
                            ) {
                                Text(lineNumberText, fontSize = 16.sp, fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.End, lineHeight = 1.5.em)
                            }
                        }
                    }
                    it()
                }
            },
            textStyle = TextStyle(color = MaterialTheme.colors.onSurface, fontSize = 16.sp, fontFamily = FontFamily.Monospace, lineHeight = 1.5.em),
            scrollState = verticalScrollState,
            cursorBrush = SolidColor(MaterialTheme.colors.secondary),
            modifier = modifier.focusRequester(focusRequester)
                .onKeyEvent {
                    altPressed = it.isAltPressed
                    if (it.type != KeyEventType.KeyUp) {
                        return@onKeyEvent false
                    }

                    keyEventConsumer(
                        it,
                        tab.path,
                        content.value,
                        lastParagraph.value,
                        {
                            coroutineScope.launch {
                                verticalScrollState.scrollBy(-16.sp.value)
                            }
                        },
                        { content.value = it }
                    )
                }
        )

        VerticalScrollbar(adapter = rememberScrollbarAdapter(verticalScrollState), modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd))
        HorizontalScrollbar(adapter = rememberScrollbarAdapter(horizontalScrollState), modifier = Modifier.fillMaxWidth().align(
            Alignment.BottomCenter))
    }

    LaunchedEffect(verticalScrollState.offset) {
        lineNumberScrollState.scrollTo(verticalScrollState.offset.toInt())
    }

    DisposableEffect(tab.path) {
        focusRequester.requestFocus()

        content.value = TextFieldValue(tab.getContent(), TextRange(tab.caretPosition()))
        verticalScrollState.offset = tab.scroll()
        setStatus("Character: ${content.value.text.length}")

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

        onDispose {
            val currentText = content.value.text
            if (currentText.isEmpty()) {
                return@onDispose
            }
            lastParagraph.value = null
            job.cancel()
            mainViewModel.updateEditorContent(tab.path, currentText, content.value.selection.start, verticalScrollState.offset, resetEditing = false)
        }
    }
}
