package jp.toastkid.yobidashi4.presentation.editor.legacy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.service.editor.TextEditor
import jp.toastkid.yobidashi4.presentation.markdown.MarkdownView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun LegacyEditorView(tab: EditorTab) {
    val koin = object : KoinComponent {
        val editor: TextEditor by inject()
        val vm: MainViewModel by inject()
    }
    val textEditor = koin.editor
    val viewModel = remember { koin.vm }

    val focusRequester = remember { FocusRequester() }

    Surface(color = MaterialTheme.colors.surface.copy(alpha = 0.5f)) {
        Column {
            Row(modifier = Modifier.weight(1f)) {
                SwingPanel(
                    background = Color.Transparent,
                    factory = {
                        textEditor.getContent()
                    },
                    modifier = Modifier.weight(1f).focusRequester(focusRequester)
                )
                if (tab.showPreview()) {
                    MarkdownView(tab, Modifier.widthIn(max = 360.dp))
                }
            }
            StatusLabel(textEditor, modifier = Modifier.height(if (viewModel.snackbarHostState().currentSnackbarData != null) 48.dp else 24.dp))
        }
    }

    DisposableEffect(tab.path) {
        focusRequester.requestFocus()
        textEditor.setText(tab.path, tab.getContent())
        textEditor.setCaretPosition(tab.caretPosition())
        onDispose {
            val currentText = textEditor.currentText() ?: return@onDispose
            viewModel.updateEditorContent(tab.path, currentText, textEditor.caretPosition(), false)
        }
    }
}

@Composable
fun StatusLabel(textEditor: TextEditor, modifier: Modifier) {
    Box(contentAlignment = Alignment.BottomEnd, modifier = modifier.fillMaxWidth().padding(end = 8.dp)) {
        val statusLabel = textEditor.statusLabel() ?: ""
        Text(statusLabel, fontSize = 16.sp)
    }
}
