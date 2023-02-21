package jp.toastkid.yobidashi4.presentation.editor.legacy

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.markdown.MarkdownView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LegacyEditorView(tab: EditorTab) {
    val editorFrame = remember { EditorFrame() }
    val focusRequester = remember { FocusRequester() }
    val viewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }

    Row() {
        SwingPanel(
            background = Color.Transparent,
            factory = {
                editorFrame.getContent()
            },
            modifier = Modifier.fillMaxHeight().weight(0.5f).focusRequester(focusRequester)
        )
        if (tab.showPreview()) {
            MarkdownView(tab, Modifier.widthIn(max = 360.dp).wrapContentWidth(Alignment.Start))
        }
    }

    DisposableEffect(tab.path) {
        focusRequester.requestFocus()
        editorFrame.setText(tab.path, tab.getContent())
        editorFrame.setCaretPosition(tab.caretPosition())
        onDispose {
            viewModel.updateEditorContent(tab.path, editorFrame.currentText(), editorFrame.caretPosition(), false)
        }
    }
}
