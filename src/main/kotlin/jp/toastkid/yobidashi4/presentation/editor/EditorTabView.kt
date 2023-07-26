package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab

@Composable
fun EditorTabView(tab: EditorTab) {
    /*
    EditorView(
        Editor(tab.path)
    )
     */
    SimpleTextEditor(
        tab.path,
        Modifier
    )
}
