package jp.toastkid.yobidashi4.presentation.editor.legacy

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.halilibo.richtext.ui.RichTextThemeIntegration
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LegacyEditorView(tab: EditorTab) {
    val editorFrame = remember { EditorFrame() }
    val focusRequester = remember { FocusRequester() }
    val showPreview = remember { mutableStateOf(true) }

    Row(modifier = Modifier.onKeyEvent {
        if (it.type == KeyEventType.KeyUp && it.isCtrlPressed && it.key == Key.M) {
            showPreview.value = showPreview.value.not()
            return@onKeyEvent true
        }
        return@onKeyEvent false
    }) {
        SwingPanel(
            background = Color.Transparent,
            factory = {
                editorFrame.getContent()
            },
            modifier = Modifier.fillMaxHeight().weight(0.5f).focusRequester(focusRequester)
        )
        if (tab.showPreview()) {
            MarkdownPreview(tab, Modifier.widthIn(max = 360.dp).wrapContentWidth(Alignment.Start))
        }
    }

    LaunchedEffect(tab.path) {
        focusRequester.requestFocus()
    }

    DisposableEffect(tab.path) {
        editorFrame.setText(tab.path, tab.getContent())
        editorFrame.setCaretPosition(tab.caretPosition())
        onDispose {
            MainViewModel.get().updateEditorContent(tab.path, editorFrame.currentText(), editorFrame.caretPosition(), false)
        }
    }
}

@Composable
private fun MarkdownPreview(tab: EditorTab, modifier: Modifier) {
    SelectionContainer(modifier = modifier) {
        val scrollState = rememberScrollState()
        Box {
            RichTextThemeIntegration(
                contentColor = { MaterialTheme.colors.onSurface }
            ) {
                RichText(
                    //style = RichTextStyle(stringStyle = stringStyle),
                    modifier = Modifier
                        .background(MaterialTheme.colors.surface.copy(alpha = 0.75f))
                        .padding(8.dp)
                        .verticalScroll(scrollState)
                ) {
                    Markdown(
                        tab.getContent(),
                        onLinkClicked = {
                            //linkBehaviorService.invoke(it)
                        }
                    )
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState), modifier = Modifier.fillMaxHeight().align(
                    Alignment.CenterEnd
                )
            )
        }
    }
}
