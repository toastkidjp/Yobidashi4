package jp.toastkid.yobidashi4.presentation.editor.legacy

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel

@Composable
fun LegacyEditorView(tab: EditorTab) {
    val editorFrame = remember { EditorFrame() }
    val focusRequester = remember { FocusRequester() }
    SwingPanel(
        background = Color.Transparent,
        factory = {
            editorFrame.setText(tab.path, tab.getContent())
            editorFrame.setCaretPosition(tab.caretPosition())
            editorFrame.getContent()
        },
        modifier = Modifier.fillMaxSize().focusRequester(focusRequester)
    )

    LaunchedEffect(tab.path) {
        focusRequester.requestFocus()
    }

    DisposableEffect(tab.path) {
        onDispose {
            MainViewModel.get().updateEditorContent(tab.path, editorFrame.currentText(), editorFrame.caretPosition(), false)
        }
    }
}
