package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.runtime.Composable
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab

@Composable
fun EditorTabView(tab: EditorTab) {
    /*SimpleTextEditor(
        tab.path,
        Modifier
    )*/
    EditorView(
        Editor(tab.path)
    )
}
